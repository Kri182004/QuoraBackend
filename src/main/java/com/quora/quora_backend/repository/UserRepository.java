package com.quora.quora_backend.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.quora.quora_backend.model.User;
public interface UserRepository extends MongoRepository<User, String> {

}
