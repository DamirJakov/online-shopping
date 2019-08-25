package pl.codementors.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.codementors.finalproject.model.Product;
import pl.codementors.finalproject.model.PurchaseForm;

@Repository
public interface PurchaseFormRepository extends JpaRepository<PurchaseForm, String> {

}
