package pl.codementors.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.codementors.finalproject.model.Product;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findAllByAvailable(boolean available);
    List<Product> findAllByOrderByPrice();
    List<Product> findAllByOrderByName();
    List<Product> findByUserId(String id);
    Optional<Product> findByUserIdAndId(String ownerId, String id);
    List<Product> findByName(String name);
    List<Product> findByNameContains(String name);

//    @Modifying
//    @Query("delete from Product p where p.id=:id")
//    void deleteById(@Param("id") String id);
//
//    @Modifying
//    @Query("update Product p set p.available=:available where p.user.id=:id")
//    void queryAllByAvailable(@Param("available") boolean available, @Param("id") String id);

    @Modifying
    @Query("update Product p set p.available=false, p.user=null where p.user.id=:id")
    void removeUserFromProductAndSetAvailable(@Param("id") String  id);

    @Query("select id from Product p where p.timestamp >= :timestamp")
    List<Product> findAllByTimeStampGreaterThan(@Param("timestamp") Timestamp timestamp);
}
