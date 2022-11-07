package com.Omer.jwt;

public class Notes {
    /*


Security Configurasyon sınıfında jwt confiurtaion ları yapıldı ve proje hazır hale getirldi.

-- Client dan gelen istek yapılan configurasyon ayarlarından gelen
                    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    fonksiyonu ile username@password post işlemiyle öncelikle jwt filtersine gitti.


                         username@password request post method
        =====>  Client------------------------------------------>Jwt filter


-- Filtereleme işleminde öncelikle gelen isteğin header kısmında Authorization keyi kontrol edildi.
      Bu kontrol gelen isteğin authorization ayarı yapılmış olup olmadığını
          String authorizationHeader = request.getHeader("Authorization"); fonskiyonu ile belirledi
                 Bu işlem ile gelen istek üzerinde token olup olmadığı anlaşıldı.

                    String token = null;
                    String username = null;

           ## İŞLEMDE AUTHORIZATON DEGERININ NULL DONEMSI

                    authorizationHeader ın null dönmesi ile gelen istek de token olmadığı anlaşıldı

                    if(authorizationHeader !=null && authorizationHeader.startsWith("Bearer")) {

                    Dolayısıyla yukarıdaki if bloğuna giremedi ve username ve token değerleri null kaldı.

                    if(username !=null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    Dolayısıyla yukarıdaki if bloğuna giremedi
                        Yukarıdaki ve herhangi bir token olmadığı için gelen isteğe devam etti

                   filterChain.doFilter(request, response);// hiçbiri değilse işleme devam et


                            username@password request
                  =====>    Filter ------------------------------------> Auth Controller


                    Auth controller a token oluşturma yolunda gelen istek "/authenticate" url sine
                        Auth manager ile haberleşerek username@password validasyonunu ile birlikte bu kullanıcıya yetki verilme gerçekleştirdi.

                     Authmanager ise Configuration sınıfınfa yaptığımız configuration sayesinde UserDetails ile haberleşerek , User Details de rwpository den db ile konuşarak bu kullanıcının validation nu sağladı
                     ve geriye exception fırlatacak durum olmaması ( validation is true) halinde controller da kalan code 'a geri dönüldü
                        Db ye kadar giden tek yönlü işlem diyagramından db den dönen cevap ile birlikte controllerda kalan satıra kadar dönüldü

                        buraya kadar ki işlem özeti


                 =====> Client ----> Filter -----> Auth Controller ------> Auth Manager ------> Auth Repository ---------> Db

                                                 Auth Controller <------ Auth Manager <------ Auth Repository <-------- Db



                        Buraya kadar ki işlemler de kullancıı validationu sağlandı ve
                        try {
                            authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(authorizeReq.getUsername(), authorizeReq.getPassword())
                                    );
                          } catch (Exception e) {
                            e.printStackTrace();
                            throw new Exception("Invalid username/password");
                          }

                      return jwtUtilService.generateToken(authorizeReq.getUsername());

                      Yukarıdaki return satırında kalan işlem ile birlikte gelen validation is true işleminin devamında
                        onaylanan kullanıcı için token oluşturmak için Token controller TokenManager ile haberleşerek token oluşturur ve dönen token 'ı kullanıcıya döner


                                            request has not token
                                                     |
                               username@password     |      generate token gateway                     username@password is valid ?                      has db this user ?                     Db, have you this user
               ======> Client --------------------> Filter -------------------------> Auth Controller -----------------------------> Auth Manager ---------------------------> Auth Repository -------------------------> Db
                                                            username@password
                                                                                                              Validation is ok.                    Manager, yes. there is in db                   Repo, Yes ı have
                      Client <--------------------- Filter <------------------------ Auth Controller <----------------------------- Auth Manager <-------------------------- Auth Repository <------------------------- Db
                                  Token                               Token             /\      |
                                                                                        |       |
                                                                                Token   |       |
                                                                                        |       |
                                                                                        |      \/
                                                                                  Token Manager ( Generate Token)









         ## İŞLEMDE AUTHORIZATON DEGERININ GÖNDERİLEN JWT AUTHORIZATION DEGERI DONEMSI

                String authorizationHeader = request.getHeader("Authorization");

                authorizationHeader değeri eğer gönderilen token değerini almışsa authenticate olan bir url ye oluşturulan token ile birlikte istek atılmış demektir.

                if(authorizationHeader !=null && authorizationHeader.startsWith("Bearer")) {

                    token = authorizationHeader.substring(7);
                    username = jwtUtilService.extractUsername(token);
                }

                Bu nedenle authorization değerinin null olmamasından dolayı yukarıdaki if bloğu içerisine girecek ve token değerinin bearer kısımsız hali yalın olarak elde edilecek ve user değişkenine göndeirlen token a ait username bilgisi çekilecektir.
                    Token manager a giderek Token varmı kontrolu sağlandı ve token bulunarak atamalar yapıldı




                                    jwt token
               ======> Client ------------------->  Filter
                                                  /\      |
                                                  |       |
                                             Yes  |       | Is there token
                                                  |       |
                                                  |      \/

                                                 Token Manager


                if(username !=null && SecurityContextHolder.getContext().getAuthentication() == null) {

		        	UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(username);
			        if(jwtUtilService.validateToken(token, userDetails)) {

			        	UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				        usernamePasswordAuthenticationToken
				        	.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			        }
		        }

		        Bundan dolayı username tokenın user adını aldı ve bu kullanıcı daha önce bu tokenla giriş yaptımı kontrolü yapıldı
		            ve user details ile haberleşerek user bilgileri çekildi bu işlemle birlikte bilgisi alınan user ve token değer i birilikte karşılaştırılarak
		                valid olup olmaıdğı token manager ile  kontrol edildi


		                Daha sonra token and user validation işleminden sonra ilgili controller a dönüldü ve repsonse cliente döndürüldü






                                                                                          Token Manager
                                                                                           |       /\
                                                                                           |       |
                                                                                        Yes|   3)  | Is valid token and userdetails
                                                                                           |       |
                                                                                           |       |
                                      What is user's user details(parameter username)             \/                Go to request contoller                      return json from controller
               ======>               <-------------------------------------------------     Filter      ------------------------------------------> Controller------------------------------>Client
      User Details Service                                  2)                            /\       |                     4)                                             5)
                                             return userDetails                           |        |
                                     ------------------------------------------------->   |        |
                                                                                          |   1)   |
                                                                                          |        |
                                                                                       Yes|        |Is there token
                                                                                          |       \/

                                                                                        Token Manager

















                                            request has not token
                                                     |
                               username@password     |      generate token gateway                     username@password is valid ?                      has db this user ?                     Db, have you this user
               ======> Client --------------------> Filter -------------------------> Auth Controller -----------------------------> Auth Manager ---------------------------> Auth Repository -------------------------> Db
                                                            username@password
                                                                                                              Validation is ok.                    Manager, yes. there is in db                   Repo, Yes ı have
                      Client <--------------------- Filter <------------------------ Auth Controller <----------------------------- Auth Manager <-------------------------- Auth Repository <------------------------- Db
                                  Token                               Token             /\      |
                                                                                        |       |
                                                                                Token   |       |
                                                                                        |       |
                                                                                        |      \/
                                                                                  Token Manager ( Generate Token)







* */
}
