/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package upeu.edu.pe.AUNaturalCosmetics.infrastructure.adapter;

import org.springframework.stereotype.Repository;
import upeu.edu.pe.AUNaturalCosmetics.app.repository.UserRepository;
import upeu.edu.pe.AUNaturalCosmetics.infrastructure.entity.UserEntity;

/**
 *
 * @author alejandromacedop
 */
@Repository
public class UserRepositoryImpl implements UserRepository{
    
      private final UserCrudRepository userCrudRepository;

    public UserRepositoryImpl(UserCrudRepository userCrudRepository) {
        this.userCrudRepository = userCrudRepository;
        
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        return userCrudRepository.save(user);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userCrudRepository.findByEmail(email).get();
    }

    @Override
    public UserEntity findById(Integer id) {
        return userCrudRepository.findById(id).get();
    }

    @Override
    public Iterable<UserEntity> getUsers() {
        return userCrudRepository.findAll();
    }

    @Override
    public void deleteUserById(Integer id) {
        userCrudRepository.deleteById(id);
    }
}

