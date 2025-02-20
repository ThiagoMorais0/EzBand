package com.baseapplication.core.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.baseapplication.core.model.Usuario;

public class Context {
	public static Usuario getUsuarioLogado() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
			Usuario user = (Usuario) authentication.getPrincipal();
			return user;
		}

		return new Usuario();
	}
}
