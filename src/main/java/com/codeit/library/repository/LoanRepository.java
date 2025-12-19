package com.codeit.library.repository;

import com.codeit.library.domain.Loan;
import com.codeit.library.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByMember(Member member);

    List<Loan> findByReturnDateIsNull();

    long countByMemberIdAndReturnDateIsNull(Long memberId);

    boolean existsByBookIdAndReturnDateIsNull(Long bookId);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END " +
           "FROM Loan l WHERE l.member.id = :memberId " +
           "AND l.returnDate IS NULL " +
           "AND l.loanDate < :cutoffDate")
    boolean existsOverdueLoan(@Param("memberId") Long memberId, @Param("cutoffDate") LocalDate cutoffDate);

    default boolean existsOverdueLoan(Long memberId) {
        LocalDate cutoffDate = LocalDate.now().minusDays(14);
        return existsOverdueLoan(memberId, cutoffDate);
    }

//    long countByBookIdAndReturnDateIsNull(Long bookId, LocalDate returnDate);
}

