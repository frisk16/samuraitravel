package com.example.samuraitravel.samuraitravel.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.samuraitravel.entity.Favorite;
import com.example.samuraitravel.samuraitravel.entity.House;
import com.example.samuraitravel.samuraitravel.entity.Reservation;
import com.example.samuraitravel.samuraitravel.entity.Review;
import com.example.samuraitravel.samuraitravel.entity.User;
import com.example.samuraitravel.samuraitravel.form.FavoriteRegisterForm;
import com.example.samuraitravel.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.samuraitravel.form.ReservationRegisterForm;
import com.example.samuraitravel.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.samuraitravel.repository.ReservationRepository;
import com.example.samuraitravel.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.samuraitravel.service.ReservationService;
import com.example.samuraitravel.samuraitravel.service.StripeService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReservationController {
	
	private final ReservationRepository reservationRepository;
	private final ReviewRepository reviewRepository;
	private final FavoriteRepository favoriteRepository;
	private final HouseRepository houseRepository;
	private final ReservationService reservationService;
	private final StripeService stripeService;

	public ReservationController(
		ReservationRepository reservationRepository,
		ReviewRepository reviewRepository,
		FavoriteRepository favoriteRepository,
		HouseRepository houseRepository,
		ReservationService reservationService,
		StripeService stripeService
	) {
		this.reservationRepository = reservationRepository;
		this.reviewRepository = reviewRepository;
		this.favoriteRepository = favoriteRepository;
		this.houseRepository = houseRepository;
		this.reservationService = reservationService;
		this.stripeService = stripeService;
	}

	@GetMapping("/reservations")
	public String index(
		@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
		@PageableDefault(page = 0, size = 10, direction = Direction.ASC) Pageable pageable,
		Model model
	) {

		User user = userDetailsImpl.getUser();
		Page<Reservation> reservationPage = this.reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);

		model.addAttribute("reservationPage", reservationPage);

		return "reservations/index";
	}

	@GetMapping("/houses/{id}/reservations/input")
	public String input(
		@PathVariable(name = "id") Integer id,
		@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
		@ModelAttribute @Validated ReservationInputForm reservationInputForm,
		BindingResult bindingResult, RedirectAttributes redirectAttributes,
		Model model
	) {

	House house = this.houseRepository.getReferenceById(id);

	List<Review> reviews = this.reviewRepository.findTop6ByHouseOrderByCreatedAtDesc(house);
	Integer totalReviews = this.reviewRepository.findByHouse(house).size();
	Integer currentUserId;

	Review currentUserReview;
	Favorite isFavorite;
	FavoriteRegisterForm favoriteRegisterForm;
	if(userDetailsImpl != null) {
		User currentUser = userDetailsImpl.getUser();
		isFavorite = this.favoriteRepository.findByHouseAndUser(house, currentUser);
		currentUserReview = this.reviewRepository.findByHouseAndUser(house, currentUser);
		currentUserId = currentUser.getId();
		favoriteRegisterForm = new FavoriteRegisterForm(currentUserId, house.getId());
	} else {
		currentUserId = 0;
		currentUserReview = null;
		isFavorite = null;
		favoriteRegisterForm = null;
	}


	Integer numberOfPeople = reservationInputForm.getNumberOfPeople();
	Integer capacity = house.getCapacity();

	if(numberOfPeople != null) {
		if(!this.reservationService.isWithinCapacity(numberOfPeople, capacity)) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "numberOfPeople", "宿泊人数が定員を超えています。");
			bindingResult.addError(fieldError);
		}
	}
	if(bindingResult.hasErrors()) {
		model.addAttribute("errorMessage", "予約内容に不備があります。");
		model.addAttribute("house", house);
		model.addAttribute("reviews", reviews);
		model.addAttribute("totalReviews", totalReviews);
		model.addAttribute("currentUserId", currentUserId);
		model.addAttribute("currentUserReview", currentUserReview);
		model.addAttribute("isFavorite", isFavorite);
		model.addAttribute("favoriteRegisterForm", favoriteRegisterForm);

		return "houses/show";
	}

	redirectAttributes.addFlashAttribute("reservationInputForm", reservationInputForm);

	return "redirect:/houses/{id}/reservations/confirm";
	}

	@GetMapping("/houses/{id}/reservations/confirm")
	public String confirm(
		@PathVariable(name = "id") Integer id,
		@ModelAttribute ReservationInputForm reservationInputForm,
		@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
		HttpServletRequest httpServletRequest,
		Model model
	) {

		House house = this.houseRepository.getReferenceById(id);
		User user = userDetailsImpl.getUser();
		LocalDate checkinDate = reservationInputForm.getCheckinDate();
		LocalDate checkoutDate = reservationInputForm.getCheckoutDate();
		Integer amount = this.reservationService.calculateAmount(checkinDate, checkoutDate, house.getPrice());

		ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm(
			house.getId(),
			user.getId(),
			checkinDate.toString(),
			checkoutDate.toString(),
			reservationInputForm.getNumberOfPeople(),
			amount
		);

		String sessionId = this.stripeService.createStripeSession(house.getName(), reservationRegisterForm, httpServletRequest);

		model.addAttribute("house", house);
		model.addAttribute("reservationRegisterForm", reservationRegisterForm);
		model.addAttribute("sessionId", sessionId);

		return "reservations/confirm";
	}

}
