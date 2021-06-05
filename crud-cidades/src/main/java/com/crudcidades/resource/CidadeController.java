package com.crudcidades.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.crudcidades.cidade.Cidade;
import com.crudcidades.cidade.CidadeEntidade;
import com.crudcidades.cidade.CidadeRepository;

@Controller
public class CidadeController {
	
	private final CidadeRepository repository;
	
	
	public CidadeController(final CidadeRepository repository) {
		this.repository = repository;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String listar(Model memoria) {
		
		memoria.addAttribute("listaCidades", this.converteCidade(repository.findAll()));
		
		return "crud";
	}
	
	private List<Cidade> converteCidade(List<CidadeEntidade> cidades){
		return cidades.stream()
				.map(cidade -> new Cidade(cidade.getNome(), cidade.getEstado()))
				.collect(Collectors.toList());
	}
	
	@RequestMapping(value = "/criar", method = RequestMethod.POST)
	public String criar(@Valid Cidade cidade, BindingResult validacao, Model memoria) {
		
		if(validacao.hasErrors()) {
			
			validacao
				.getFieldErrors()
				.forEach(error -> memoria.addAttribute(
							error.getField(), error.getDefaultMessage()));
			
			memoria.addAttribute("nomeInformado", cidade.getNome());
			memoria.addAttribute("estadoInformado", cidade.getEstado());
			memoria.addAttribute("listaCidades", this.converteCidade(repository.findAll()));
			
			return "/crud";
								
		}else {
			
			repository.save(cidade.clonar());
		}
		
	
		
		return "redirect:/";
	}
	
	@RequestMapping(value = "/excluir", method = RequestMethod.GET)
	public String excluir(@RequestParam String nome, @RequestParam String estado)  {
		
		var cidadeEstadoEncontrada = repository.findByNomeAndEstado(nome, estado);
		
		cidadeEstadoEncontrada.ifPresent(repository::delete);
		
		return "redirect:/";
	}
	
	@RequestMapping(value = "/preparaAlterar", method = RequestMethod.GET)
	public String preparaAlterar(@RequestParam String nome, @RequestParam String estado, Model memoria) {
		
		var cidadeAtual = repository.findByNomeAndEstado(nome, estado);
		
		cidadeAtual.ifPresent(cidadeEncontrada -> {
			memoria.addAttribute("cidadeAtual", cidadeEncontrada);
			memoria.addAttribute("listaCidades", this.converteCidade(repository.findAll()));
		});
		
		return "/crud";
	}
	
	@RequestMapping(value = "/alterar", method = RequestMethod.POST)
	public String alterar(@RequestParam String nomeAtual, @RequestParam String estadoAtual, Cidade cidade,
			BindingResult validacao,
			Model memoria) {
		
		var cidadeAtual = repository.findByNomeAndEstado(nomeAtual, estadoAtual);
		
		if(cidadeAtual.isPresent()) {
			
			var cidadeEncontrada = cidadeAtual.get();
			cidadeEncontrada.setNome(cidade.getNome());
			cidadeEncontrada.setEstado(cidade.getEstado());
			
			repository.saveAndFlush(cidadeEncontrada);
		}
		
		return "redirect:/";
	}
	
}
