package com.lcwd.user.service.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.exceptions.ResourceNotFoundException;
import com.lcwd.user.service.external.services.HotelService;
import com.lcwd.user.service.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private HotelService hotelService;
	
	private Logger logger=LoggerFactory.getLogger(UserServiceImpl.class);
	@Override
	public User saveUser(User user) {
		String randomUserId=UUID.randomUUID().toString();
		user.setUserId(randomUserId);
		log.info("user present");
		return userRepository.save(user);
	}

	@Override
	public List<User> getAllUser() {
		// TODO Auto-generated method stub
		return userRepository.findAll();
	}

	@Override
	public User getUser(String userId) {
		User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user with given id not present on server !!:"+userId));
		
		
	Rating[] ratingsOfUser = restTemplate.getForObject("http://RATINGSERVICE/ratings/users/"+user.getUserId(),Rating[].class);
		
	logger.info("{}",ratingsOfUser);
	
List<Rating> ratings = Arrays.stream(ratingsOfUser).toList();
	
	List<Rating> ratingList=ratings.stream().map(rating->
	{
//		ResponseEntity<Hotel> forEntity=restTemplate.getForEntity("http://HOTELSERVICE/hotels/"+rating.getHotelId(), Hotel.class);
//		Hotel hotel=forEntity.getBody();
		
		
		Hotel hotel=hotelService.getHotel(rating.getHotelId());
		
	//	logger.info("resopnse status code:{}",forEntity.getStatusCode());
		rating.setHotel(hotel);
		return rating;
		
	}).collect(Collectors.toList());			
	
	
	
	user.setRatings(ratingList);
		return user;
	}
}