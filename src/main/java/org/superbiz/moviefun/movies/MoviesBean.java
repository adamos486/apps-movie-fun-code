/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.superbiz.moviefun.movies;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository public class MoviesBean {

  @PersistenceContext(unitName = "persist-movies")
  @Autowired
  @Qualifier("createMoviesEntityBean")
  private EntityManager createMoviesEntityBean;

  public Movie find(Long id) {
    return createMoviesEntityBean.find(Movie.class, id);
  }

  public void addMovie(Movie movie) {
    createMoviesEntityBean.persist(movie);
  }

  @Transactional public void editMovie(Movie movie) {
    createMoviesEntityBean.merge(movie);
  }

  @Transactional public void deleteMovie(Movie movie) {
    createMoviesEntityBean.remove(movie);
  }

  @Transactional public void deleteMovieId(long id) {
    Movie movie = createMoviesEntityBean.find(Movie.class, id);
    deleteMovie(movie);
  }

  public List<Movie> getMovies() {
    CriteriaQuery<Movie> cq = createMoviesEntityBean.getCriteriaBuilder().createQuery(Movie.class);
    cq.select(cq.from(Movie.class));
    return createMoviesEntityBean.createQuery(cq).getResultList();
  }

  public List<Movie> findAll(int firstResult, int maxResults) {
    CriteriaQuery<Movie> cq = createMoviesEntityBean.getCriteriaBuilder().createQuery(Movie.class);
    cq.select(cq.from(Movie.class));
    TypedQuery<Movie> q = createMoviesEntityBean.createQuery(cq);
    q.setMaxResults(maxResults);
    q.setFirstResult(firstResult);
    return q.getResultList();
  }

  public int countAll() {
    CriteriaQuery<Long> cq = createMoviesEntityBean.getCriteriaBuilder().createQuery(Long.class);
    Root<Movie> rt = cq.from(Movie.class);
    cq.select(createMoviesEntityBean.getCriteriaBuilder().count(rt));
    TypedQuery<Long> q = createMoviesEntityBean.createQuery(cq);
    return (q.getSingleResult()).intValue();
  }

  public int count(String field, String searchTerm) {
    CriteriaBuilder qb = createMoviesEntityBean.getCriteriaBuilder();
    CriteriaQuery<Long> cq = qb.createQuery(Long.class);
    Root<Movie> root = cq.from(Movie.class);
    EntityType<Movie> type = createMoviesEntityBean.getMetamodel().entity(Movie.class);

    Path<String> path = root.get(type.getDeclaredSingularAttribute(field, String.class));
    Predicate condition = qb.like(path, "%" + searchTerm + "%");

    cq.select(qb.count(root));
    cq.where(condition);

    return createMoviesEntityBean.createQuery(cq).getSingleResult().intValue();
  }

  public List<Movie> findRange(String field, String searchTerm, int firstResult, int maxResults) {
    CriteriaBuilder qb = createMoviesEntityBean.getCriteriaBuilder();
    CriteriaQuery<Movie> cq = qb.createQuery(Movie.class);
    Root<Movie> root = cq.from(Movie.class);
    EntityType<Movie> type = createMoviesEntityBean.getMetamodel().entity(Movie.class);

    Path<String> path = root.get(type.getDeclaredSingularAttribute(field, String.class));
    Predicate condition = qb.like(path, "%" + searchTerm + "%");

    cq.where(condition);
    TypedQuery<Movie> q = createMoviesEntityBean.createQuery(cq);
    q.setMaxResults(maxResults);
    q.setFirstResult(firstResult);
    return q.getResultList();
  }

  public void clean() {
    createMoviesEntityBean.createQuery("delete from Movie").executeUpdate();
  }
}
