package com.uis.ejeMvc.service;

import com.uis.ejeMvc.model.Tipodocumento;
import com.uis.ejeMvc.repository.TipodocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class TipodocumentoService implements TipodocumentoRepository{

    @Autowired
    private TipodocumentoRepository tipodocumentoRepository;


    @Override
    public void flush() {

    }

    @Override
    public <S extends Tipodocumento> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Tipodocumento> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Tipodocumento> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Tipodocumento getOne(Long aLong) {
        return null;
    }

    @Override
    public Tipodocumento getById(Long aLong) {
        return null;
    }

    @Override
    public Tipodocumento getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Tipodocumento> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Tipodocumento> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Tipodocumento> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Tipodocumento> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Tipodocumento> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Tipodocumento> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Tipodocumento, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Tipodocumento> S save(S entity) {
        return tipodocumentoRepository.save(entity);
    }

    @Override
    public <S extends Tipodocumento> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Tipodocumento> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Tipodocumento> findAll() {
        return tipodocumentoRepository.findAll();
    }

    @Override
    public List<Tipodocumento> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Tipodocumento entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Tipodocumento> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Tipodocumento> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Tipodocumento> findAll(Pageable pageable) {
        return null;
    }
}
