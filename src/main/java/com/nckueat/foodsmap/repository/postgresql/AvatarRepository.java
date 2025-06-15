package com.nckueat.foodsmap.repository.postgresql;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nckueat.foodsmap.model.entity.Avatar;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
}
