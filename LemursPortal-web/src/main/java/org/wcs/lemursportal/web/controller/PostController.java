package org.wcs.lemursportal.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.wcs.lemursportal.model.post.Post;
import org.wcs.lemursportal.model.post.Thematique;
import org.wcs.lemursportal.service.post.PostService;
import org.wcs.lemursportal.service.post.ThematiqueService;

@Controller
@Transactional
public class PostController extends BaseController{

	@Autowired
	private PostService postService;
	
	@Autowired
	private ThematiqueService thematiqueService;
	
	
	@GetMapping(value="/secured/post/create")
	@PreAuthorize("hasAnyRole('EXPERT','MODERATEUR', 'ADMIN')")
	public String create(Authentication authentication, Model model){
		List<Thematique> listethematique = thematiqueService.findAll();
		model.addAttribute("listeThematique", listethematique);
		model.addAttribute(new Post());
		return "getFormPost";
	}
	
	@PostMapping(value="/secured/post")
	@PreAuthorize("hasAnyRole('EXPERT','MODERATEUR', 'ADMIN')")
	public String submit(Authentication authentication, Model model, 
			@ModelAttribute Post post, 
			BindingResult results){		
		//ValidationUtils.rejectIfEmptyOrWhitespace(results, "libelle", "validation.mandatory");
		if(results.hasErrors()){
			return "forward:post/post-form";
		}
		//thematiqueService.saveOrUpdate(authentication.getName(), thematique);
		//post.set
		postService.insert(post);
		return "redirect:/";
	}
	
	
	@PostMapping(value="/post/search")	
	public String search(@RequestParam(name="pattern", required=false) String pattern, Model model){
		Page<Post> p = postService.search(new PageRequest(0, 10), pattern);
		model.addAttribute("posts", postService.search(new PageRequest(0, 10), pattern));
		//System.out.println("*************** " + pattern + "  " +p.getSize());
		return "results";
	}
	
	@GetMapping(value="/post/show/{idPost}")	
	public String showPost(@PathVariable(name="idPost", required=true) Integer idPost, @RequestParam(required=false, defaultValue="0") Integer page, Model model){
		Post p = postService.findPostById(idPost);
		if(page == null || page < 1){
			page = 0;
		}else{
			page = page - 1;
		}
		Page<Post> responsesPage = postService.getQuestionResponses(idPost, new PageRequest(page, BaseController.DERNIERES_QUESTIONS_PAGE_SIZE));
		model.addAttribute("post", p);
		model.addAttribute("responsesPage", responsesPage);
		setPagination(page, responsesPage, model);
		return "showPost";
	}
	 
	
	@PostMapping(value="/secured/post/reponse")
	@PreAuthorize("hasAnyRole('EXPERT','MODERATEUR', 'ADMIN')")
	public String submitReponse(Authentication authentication, Model model, 
			@ModelAttribute Post post, 
			BindingResult results){		
		//ValidationUtils.rejectIfEmptyOrWhitespace(results, "libelle", "validation.mandatory");
		if(results.hasErrors()){
			return "forward:post/post-form";
		}
		
		Post parent = new Post();
		parent.setId(post.getId());
		post.setId(null);
		post.setParentId(parent.getId());
		post.setTitle("");
		postService.insert(post);
		return "redirect:/post/show/"+parent.getId();
	}
}