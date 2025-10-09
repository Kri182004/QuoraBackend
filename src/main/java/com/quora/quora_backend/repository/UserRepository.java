package com.quora.quora_backend.repository;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.quora.quora_backend.model.User;
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User>findByUsername(String username);

}
//we used interfaces for creating repositories because an intercase is a contract.
//it defines a set of rules for  class to follow without saying how the rules are carried out.
//Spring Data then takes this contract and automatically
//generates the code for us that handles the actual communication with the database.


// we extended MongoRepository because we wanted to inherit its pre-defined methods.
// Think of it like this: MongoRepository is a powerful,ready-to-use template that already has methods like save(), findAll(), and findById().
// By extending it, we are getting all those methods for free, 
//so we don't have to write them ourselves.