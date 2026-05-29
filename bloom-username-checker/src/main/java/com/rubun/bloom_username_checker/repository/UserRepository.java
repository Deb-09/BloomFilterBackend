package com.rubun.bloom_username_checker.repository;
import com.rubun.bloom_username_checker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
}


//JpaRepository
//    │  save(), saveAll(), flush()
//    │  findAll(Sort), findAll(Pageable)
//    │
//    └── extends PagingAndSortingRepository
//            │  findAll(Sort), findAll(Pageable)
//            │
//            └── extends CrudRepository
//                    │  save(), findById(), existsById()
//                    │  findAll(), count(), delete()
//                    │
//                    └── extends Repository  ← the root marker interface
