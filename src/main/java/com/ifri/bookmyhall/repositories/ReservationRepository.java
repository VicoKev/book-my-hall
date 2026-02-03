package com.ifri.bookmyhall.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ifri.bookmyhall.models.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
