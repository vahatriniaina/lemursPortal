package org.wcs.lemursportal.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wcs.lemursportal.model.authentication.UserRole;
import org.wcs.lemursportal.model.post.Post;
import org.wcs.lemursportal.model.post.PostView;
import org.wcs.lemursportal.model.post.Thematique;
import org.wcs.lemursportal.model.user.UserInfo;
import org.wcs.lemursportal.model.user.UserType;
import org.wcs.lemursportal.repository.post.PostCrudRepository;
import org.wcs.lemursportal.repository.post.PostViewCrudRepository;
import org.wcs.lemursportal.repository.post.ThematiqueCrudRepository;
import org.wcs.lemursportal.repository.user.UserRepository;
import org.wcs.lemursportal.repository.user.UserTypeRepository;
import org.wcs.lemursportal.service.user.UserInfoService;

/**
 * @author Mikajy <mikajy401@gmail.com>
 *
 */
@Controller
//@Transactional
public class DataTestController {
	
	private static final int NB_THEMATIQUE_MAX = 20;
	private static final int NB_QUESTION_MAX = 100;
	private static final int NB_RESPONSE_MAX = 900;
	private static final int NB_POST_VIEW_MAX = 1000;
	private final Random random = new Random();
	@Autowired UserInfoService userInfoService;
	@Autowired UserRepository userRepository;
	
	@Autowired ThematiqueCrudRepository thematiqueCrudRepository;
	@Autowired PostCrudRepository postCrudRepository; 
	@Autowired PostViewCrudRepository postViewCrudRepository;
	@Autowired UserTypeRepository userTypeRepository;
	
	@GetMapping(value="/admin/generate_topicsandpost")
	public @ResponseBody String generateDataForTest(Authentication authentication, HttpServletRequest request){
		UserInfo currentUser = userInfoService.getByEmail(authentication.getName());

		List<UserInfo> experts = userTypeRepository.findUsers(UserRole.EXPERT);
		StringBuilder sb = new StringBuilder("Erreur! Aucun experts enregistré dans la base de données !");
		if(experts != null && !experts.isEmpty()){
			List<Thematique> thematiques = generateThematique(currentUser, experts);
			sb = new StringBuilder("<h2>Recap : </h2>");
			sb.append(thematiques.size() + " générés dont : <ul>");
			List<Post> thematiquePosts = new ArrayList<>();
			for(Thematique thematique: thematiques){
				thematiquePosts = generatePosts(thematique, currentUser);
				generateResponses(thematiquePosts);
				for(Post post: thematiquePosts){
					List<PostView> postViews = simulatePostView(thematique, post, currentUser);
					postViewCrudRepository.save(postViews);
				}
				sb.append("<li>" + thematique.getLibelle() + "=> " + thematiquePosts.size() +" posts </li>");
				
			}
			sb.append("</ul>");
		}
		
		return sb.toString();
	}
	
	@GetMapping(value="/admin/generate_topics")
	public @ResponseBody String generateDataTopics(Authentication authentication, HttpServletRequest request){
		UserInfo currentUser = userInfoService.getByEmail(authentication.getName());

		UserInfo expert = addExpert();
		StringBuilder sb = new StringBuilder("Erreur! Aucun experts enregistré dans la base de données !");
		List<String> topics = Arrays.asList("Behavior ","Threats /conservation issues","Vocalization","Ecology","Genetics","Locomotion",
				"Taxonomy","Conservation Status","Subfossiles","Lemur conservation and research administrative","Environmental Education",
				"Lemur in captivity","Lemur Medicine/Biomedical assessement", "Species distribution and occurrences",
				"Nocturnal species","Diurnal species","Reintroduction and translocation","lemur conservation success ",
				"Nutrition","Forest fragment","Parasites","Others");
		
			List<Thematique> thematiques = generateThematiques(currentUser, expert, topics);
			sb = new StringBuilder("<h2>Recap : </h2>");
			sb.append(thematiques.size() + " générés dont : <ul>");
		
		return sb.toString();
	}
	
	
	private UserInfo addExpert(){
		UserInfo u = new UserInfo();
		u.setEmail("ratsimbazafy@yopmail.com");
		u.setNom("Ratsimbazafy");
		u.setPrenom("Jonah");
		u.setDateInscription(Calendar.getInstance().getTime());
		u.setEnabled(true);
		if(u.getRoles() == null || u.getRoles().isEmpty()){
			u.setRoles(new java.util.HashSet<UserType>());
			u.getRoles().add(UserRole.EXPERT.getUserType());
		}
		userRepository.save(u);
		return u;
		
	}
	
	private List<Thematique> generateThematiques(UserInfo currentUser, UserInfo expert, List<String> nameThematiques){
		List<Thematique> thematiques = new ArrayList<>();
		for(String nameThematique:nameThematiques){
			Thematique t = new Thematique();
			t.setCreatedBy(currentUser);
			t.setCreationDate(Calendar.getInstance().getTime());
			t.setDescription(nameThematique);
			t.setLibelle(nameThematique);
			Set<UserInfo> managers = new java.util.HashSet<>();
			managers.add(expert);
			t.setManagers(managers);
			thematiques.add(t);
		}
		thematiqueCrudRepository.save(thematiques);
		return thematiques;
	}
	
	private List<Thematique> generateThematique(UserInfo currentUser, List<UserInfo> experts){
		List<Thematique> thematiques = new ArrayList<>();
		int nbThematique = random.nextInt(NB_THEMATIQUE_MAX);
		while(nbThematique <= 0){
			nbThematique = random.nextInt(NB_THEMATIQUE_MAX);
		}
		for(int i=0;i< nbThematique;i++){
			int indexExpert = random.nextInt(experts.size());
			if(indexExpert < 0 || indexExpert == experts.size()){
				indexExpert = 0;
			}
			Thematique t = new Thematique();
			t.setCreatedBy(currentUser);
			t.setCreationDate(Calendar.getInstance().getTime());
			t.setDescription("Desc du thematique_" + i);
			t.setLibelle("Topic_" + i);
			Set<UserInfo> managers = new java.util.HashSet<>();
			managers.add(experts.get(indexExpert));
			t.setManagers(managers);
			thematiques.add(t);
		}
		thematiqueCrudRepository.save(thematiques);
		return thematiques;
	}
	
	private List<Post> generateResponses(List<Post> questions){
		List<Post> posts = new ArrayList<>();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		for(Post question: questions){
			int nbMessage = -1;
			while(nbMessage < 0){
				nbMessage = random.nextInt(NB_RESPONSE_MAX);
			}
			for(int i=0; i< nbMessage; i++ ){
				Post message = new Post();
				calendar.add(Calendar.MINUTE, i);
				Date creationDate = calendar.getTime();
				message.setCreationDate(creationDate);
				message.setTitle("Response " + i + " de question " + question.getTitle());
				message.setBody("Description lavalava ihany ho an'i Message numéro " + i + " La Juve, le pire tirage pour Monaco ? Un derby madrilène explosif !");
				message.setOwnerId(question.getOwnerId());
				message.setParentId(question.getId());
				message.setParent(question);
				message.setThematiqueId(question.getThematiqueId());
//				message.setThematique(question.getThematique());
				posts.add(message);
			}
		}
		
		postCrudRepository.save(posts);
		return posts;
	}
	
	private List<Post> generatePosts(Thematique thematique, UserInfo user){
		List<Post> posts = new ArrayList<>();
		//Test: création de post pour le nouveau thématique
		int nbMessage = -1;
		while(nbMessage < 0){
			nbMessage = random.nextInt(NB_QUESTION_MAX);
		}
		
		
		Calendar calendar = Calendar.getInstance();
		int maxMonth = calendar.get(Calendar.MONTH);
		
		for(int i=0, calendarMonth = -1; i< nbMessage; i++ ){
			Post message = new Post();
			while(calendarMonth <  0 ){
				calendarMonth = random.nextInt(maxMonth);
			}
			calendar.set(Calendar.MONTH, calendarMonth);
			Date creationDate = calendar.getTime();
			message.setCreationDate(creationDate);
			message.setTitle("Question " + i + " " + thematique.getLibelle());
			message.setBody("Description lavalava ihany ho an'i Message numéro " + i + " La Juve, le pire tirage pour Monaco ? Un derby madrilène explosif !");
			message.setOwnerId(user.getId());
			message.setOwner(user);
			message.setThematiqueId(thematique.getId());
//			message.setThematique(thematique);
			posts.add(message);
		}
		postCrudRepository.save(posts);
		return posts;
	}
	
	private List<PostView> simulatePostView(Thematique thematique, Post post, UserInfo user){
		List<PostView> postViews = new ArrayList<>();
		int nbView = random.nextInt(NB_POST_VIEW_MAX);
		while(nbView < 0){
			nbView = random.nextInt(NB_POST_VIEW_MAX);
		}
		for(int i=0;i< nbView; i++){
			PostView postView = new PostView();
			postView.setPostId(post.getId());
			postView.setThematiqueId(thematique.getId());
			postView.setViewBy(user.getId());
			postView.setViewDate(Calendar.getInstance().getTime());
			postViews.add(postView);
		}
		return postViews;
	}
	
}
