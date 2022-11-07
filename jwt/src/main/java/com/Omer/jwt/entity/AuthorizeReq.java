package com.Omer.jwt.entity;

import lombok.*;

@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizeReq {

	private String username;
	private String password;
}
