package org.example.backend.repository;

import org.example.backend.model.entity.Address;
import org.example.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);

    // Find the default address for a user
    Address findByUserAndIsDefaultTrue(User user);
}
