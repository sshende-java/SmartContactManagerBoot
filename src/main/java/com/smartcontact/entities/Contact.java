package com.smartcontact.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "CONTACT")
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;
	
	@NotBlank(message="* User Name cannot be blank !!")
	@Size(min=3,max=15,message="* Name should range between 3 to 15 characters !!")
	private String name;
	
	@NotBlank(message="* Nickname cannot be blank !!")
	@Size(min=3,max=10,message="* NickName should range between 3 to 10 characters !!")
	private String secondName;
	
	@NotBlank(message="* Please enter work details !!")
	private String work;
	
	@NotBlank(message="Email cannot be blank !!")
	private String email;
	
	@NotBlank(message="* Please Enter Phone number !!")
	@Size(min=10,max=10,message="* Invalid Phone Number !!")
	private String phone;
	
	private String imageUrl;
	
	@NotBlank(message="* Please Enter something about Yourself !!")
	@Column(length = 1000)
	private String description;
	
	@ManyToOne()
	@JsonIgnore						//JsonIgnore means this property will be ignored at time of serialization i.e conversion 
	private User user;				//why ignore --> bcoz contact contains user,user contains contact then again contact contains user ... which results in endless loop
	
	public Contact() {
		super();
		// TODO Auto-generated constructor stub
	}


	public int getcId() {
		return cId;
	}


	public void setcId(int cId) {
		this.cId = cId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getSecondName() {
		return secondName;
	}


	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}


	public String getWork() {
		return work;
	}


	public void setWork(String work) {
		this.work = work;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}

	
	
	
}
