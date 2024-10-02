package com.baseapplication.core.controller;

import com.baseapplication.core.dto.RetornoDTO;
import com.baseapplication.core.service.BandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/banda")
public class BandaController {

    @Autowired
    private BandaService bandaService;

    @GetMapping("/getInfo")
    public RetornoDTO getInfo(@RequestParam Long idBanda){
        try{
            return RetornoDTO.success(bandaService.getInfo(idBanda));
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @PostMapping("/cadastrarUsuario")
    public RetornoDTO cadastrarUsuario(@RequestParam Long idBanda,
                                       @RequestParam Long idUsuario,
                                       @RequestParam String instrumentos){
        try{
            bandaService.cadastrarUsuario(idBanda, idUsuario, instrumentos);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @PostMapping("/expulsarUsuario")
    public RetornoDTO cadastrarUsuario(@RequestParam Long idBanda, @RequestParam Long idUsuario){
        try{
            bandaService.expulsarUsuario(idBanda, idUsuario);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @GetMapping("/getMembros")
    public RetornoDTO getMembros(@RequestParam Long idBanda){
        try{
            return RetornoDTO.success(bandaService.buscarMembros(idBanda));
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }



}
