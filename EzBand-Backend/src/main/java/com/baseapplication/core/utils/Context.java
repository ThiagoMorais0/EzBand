package com.baseapplication.core.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.dto.UserPrincipal;
import com.baseapplication.core.model.Usuario;

public class Context {
	
	private static UsuarioDao usuarioDao;
	
	public static void setUsuarioDao(UsuarioDao dao) {
		usuarioDao = dao;
	}
	
	public static Usuario getUsuarioLogado() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			if (usuarioDao != null) {
				Usuario usuario = usuarioDao.findById(userPrincipal.getId()).orElse(null);
				if (usuario != null) {
					return usuario;
				}
			}
		}

		return new Usuario();
	}
}
