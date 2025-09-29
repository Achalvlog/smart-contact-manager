package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.smart.helper.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;


@Controller
@RequestMapping("/user")
public class UserController { 
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	
	//method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		String userName=principal.getName();
		System.out.println("username--"+userName);
		User user=userRepository.getUserByUserName(userName);
		System.out.println("USER"+user);
		 model.addAttribute("user",user);
		
	}
	
	//dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
	
	//open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,
			Principal principal,HttpSession session) {
		try {
		String name=principal.getName();
		User user=this.userRepository.getUserByUserName(name);
		
		//processing and uploading file...
		
		if(file.isEmpty()) {
			System.out.println("file is empty or not");
			contact.setImage("contact.jpg");
		}else {
			contact.setImage(file.getOriginalFilename());
			
			
			
		File saveFile=new ClassPathResource("static/img").getFile();
		//kal issi k aage se coding krni hai 14:03 
		Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		
		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		System.out.println("image is uploaded ");
		
		}
		contact.setUser(user);
		user.getContacts().add(contact);
		this.userRepository.save(user);
		
		System.out.println("data" +contact);
		
		System.out.println("added to data base");
		
		session.setAttribute("message", new Message("your contact is added !! add more","success"));
		//message success 
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("error"+e.getMessage());
			e.printStackTrace();
			//errror message 
			session.setAttribute("message", new Message("Something went wrong try again","danger"));
		}
		return "normal/add_contact_form";
	}
	//Show contact handler
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model m,Principal principal) {
		m.addAttribute("title", "Show User Contacts");
		String userName=principal.getName();
		 User user=this.userRepository.getUserByUserName(userName);
		
		Pageable pageable=PageRequest.of(page, 5);
		Page<Contact> contacts=this.contactRepository.findContactsByUser(user.getId(),pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages",contacts.getTotalPages());
	
		return "normal/show_contacts";
	}
	//showing particular contact
	@RequestMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model,Principal principal) {
	    System.out.println("cId: " + cId);
	    Contact contact = this.contactRepository.findById(cId)
	                           .orElseThrow(() -> new RuntimeException("Contact not found"));
	    
	   String userName= principal.getName();
	   User user=this.userRepository.getUserByUserName(userName);
	   
	    if(user.getId()==contact.getUser().getId())
	    {
	    	model.addAttribute("title",contact.getName());
	    	model.addAttribute("contact", contact);
	    }
	    return "normal/contact_detail";
	}
	
	//Delete Contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId,Model model,HttpSession session) {
		//Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		//Contact contact=contactOptional.get();
		//use check so that not anyone delete it
		System.out.println("CID" +cId);
		Contact contact=this.contactRepository.findById(cId).get();
		System.out.println("contact"+contact.getcId());
		contact.setUser(null);
		
		this.contactRepository.delete(contact);
		System.out.println("deleted");
		session.setAttribute("message", new Message("contact delete successfully","success"));
		return "redirect:/user/show-contacts/0";
	}
	//update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model m) {
		
		m.addAttribute("title","update Contact");
		
		Contact contact=this.contactRepository.findById(cid).get();
		m.addAttribute("contact", contact);
		return "normal/update_form";
	}
	
	//Your profile handler
	@GetMapping("/profile")
	public String yourProfie(Model model) {
		
		
		model.addAttribute("title","Profile Page");
		return "normal/profile";
		
	}

}
