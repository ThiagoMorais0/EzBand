package com.baseapplication.core.controller;

import com.baseapplication.core.dto.DenunciaDTO;
import com.baseapplication.core.service.DenunciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/denuncia")
public class DenunciaController {

	@Autowired
	private DenunciaService denunciaService;

	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(@RequestBody DenunciaDTO denuncia) {
		denunciaService.registrarDenuncia(denuncia);
		return ResponseEntity.ok("Denúncia registrada com sucesso");
	}
}
