package com.crudcidades.resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CidadeContoller {

	@GetMapping("/")
	public String listar() {
		return "crud.html";
	}
}
