package com.github.gudiasoliveira.SellingsManagement.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.gudiasoliveira.SellingsManagement.dao.SellerDao;
import com.github.gudiasoliveira.SellingsManagement.models.Seller;

@RestController
@RequestMapping("/seller")
public class SellerController {
	
	@Autowired
	SellerDao sellerDao;
	
	@PostMapping("/")
	@ResponseBody
	public Seller create(@Valid @RequestBody Seller seller) {
		return sellerDao.save(seller);
	}
	
	@GetMapping("/")
	public List<Seller> getAll() {
		return toList(sellerDao.findAll());
	}
	
	@GetMapping("/{id}")
	public Seller getById(@PathVariable long id) {
		try {
			return sellerDao.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new SellerNotFoundException();
		}
	}
	
	@PutMapping("/{id}")
	public Seller update(@PathVariable long id, @Valid @RequestBody Seller seller) {
		seller.setId(id);
		return sellerDao.save(seller);
	}
	
	@DeleteMapping("/{id}")
	public Map<String, Object> delete(@PathVariable long id) {
		try {
			sellerDao.deleteById(id);
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			return response;
		} catch (EmptyResultDataAccessException e) {
			throw new SellerNotFoundException();
		}
	}
	
	private static <T> List<T> toList(Iterable<T> it) {
		List<T> list = new ArrayList<>();
		for (T el : it)
			list.add(el);
		return list;
	}
	
	@SuppressWarnings("serial")
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	static class SellerNotFoundException extends RuntimeException {
		SellerNotFoundException() {
			super("No seller exists with the given id!");
		}
	}
}
