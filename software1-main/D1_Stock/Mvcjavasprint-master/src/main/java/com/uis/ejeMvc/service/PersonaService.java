package com.uis.ejeMvc.service;

import com.uis.ejeMvc.model.Persona;
import com.uis.ejeMvc.repository.PersonaRepositoryInter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaService{
    @Autowired
    private PersonaRepositoryInter personaRepositoryInter;

    public Persona savePersona(Persona persona){

        return personaRepositoryInter.save(persona);

    }

    public Optional<Persona> findById(Long id){
        return personaRepositoryInter.findById(id);
    }

    public Page<Persona> getAllPersona(Integer page, Integer size, Boolean eneablePagination){
        return personaRepositoryInter.findAll(eneablePagination ? PageRequest.of(page, size): Pageable.unpaged());
    }

    public void deletePersona(Long id){
        personaRepositoryInter.deleteById(id);
    }

    public Persona editPersona(Persona persona){
        if(persona.getId() != 0 && personaRepositoryInter.existsById(persona.getId()))
        {
            return personaRepositoryInter.save(persona);
        }
        return null;
    }


    public boolean existById(Long id) {
        return personaRepositoryInter.existsById(id);
    }

    public List<Persona> findAll() {
        return personaRepositoryInter.findAll();
    }
}

/*
  @Override
    public void flush() {

    }

    @Override
    public <S extends Persona> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Persona> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Persona> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Persona getOne(Long aLong) {
        return null;
    }

    @Override
    public Persona getById(Long aLong) {
        return null;
    }

    @Override
    public Persona getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Persona> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Persona> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Persona> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Persona> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Persona> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Persona> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Persona, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Persona> S save(S entity) {
        return personaRepositoryInter.save(entity);
    }

    @Override
    public <S extends Persona> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Persona> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Persona> findAll() {
        return personaRepositoryInter.findAll();
    }

    @Override
    public List<Persona> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public List<Persona> findAll(Sort sort) {
        return personaRepositoryInter.findAll(sort);
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long id) {
        personaRepositoryInter.deleteById(id);
    }

    @Override
    public void delete(Persona entity) {
        personaRepositoryInter.delete(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Persona> entities) {
        personaRepositoryInter.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        personaRepositoryInter.deleteAll();
    }

    @Override
    public Page<Persona> findAll(Pageable pageable) {
        return null;
    }
 */
