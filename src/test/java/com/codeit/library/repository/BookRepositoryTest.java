package com.codeit.library.repository;

import com.codeit.library.domain.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // JPA 관련 컴포넌트만 로딩
@DisplayName("도서 Repository 테스트")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Nested
    @DisplayName("검색 관련 기능")
    class search {
        @Test
        @DisplayName("저자 이름으로 책을 검색할 수 있다.")
        void findByAuthor() {
            // given
            bookRepository.save(new Book("클린 코드", "로버트 마틴", "111" , 30000));
            bookRepository.save(new Book("클린 아키텍처", "로버트 마틴", "222" , 32000));
            bookRepository.save(new Book("리팩토링", "마틴 파울ㄹ러", "333" , 35000));

            // when
            List<Book> books = bookRepository.findByAuthor("로버트 마틴");

            // then
            assertThat(books).hasSize(2);
            assertThat(books)
                    .extracting("title")
                    .containsExactlyInAnyOrder("클린 코드", "클린 아키텍처"); // containsExactly를 쓰는 건 순서까지 일치해야 함


        }

        @Test
        @DisplayName("ISBN으로 책을 검색할 수 있다.")
        void findByIsbn() {
            //given
            bookRepository.save(new Book("클린 코드", "로버트 마틴", "987-1234567890" , 30000));

            // when
            Optional<Book> found = bookRepository.findByIsbn("987-0132456266");

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getTitle()).isEqualTo("클린 코드");
        }
    }

    @Nested
    @DisplayName("직접 작성한 JPQL 검증")
    class ManualQuery {
        @Test
        @DisplayName("가격이 높고 최근에 출판된 책을 조회한다.")
        void findExpensiveRecentBooks() {
            // given
            LocalDate cutoffDate = LocalDate.of(2020, 1, 1);

            Book oldExpensiveBook = new Book("오래된 비싼 책", "저자1", "111", 60000,
                    LocalDate.of(2019, 1, 1));
            Book newExpensiveBook = new Book("최신 비싼 책", "저자2", "222", 60000,
                    LocalDate.of(2021, 1, 1));
            Book newCheapBook = new Book("최신 싼 책", "저자3", "333", 10000,
                    LocalDate.of(2021, 1, 1));

            bookRepository.save(oldExpensiveBook);
            bookRepository.save(newExpensiveBook);
            bookRepository.save(newCheapBook);

            // when
            List<Book> books = bookRepository.findExpensiveRecentBooks(50000, cutoffDate);

            // then
            assertThat(books).hasSize(1);
            assertThat(books.get(0).getTitle()).isEqualTo("최신 비싼 책");
        }
    }

    @Test
    @DisplayName("저자만 지정하여 검색한다")
    void searchByAuthorOnly() {
        // given
        bookRepository.save(new Book("클린 아키텍처", "로버트 마틴", "222" , 32000));
        bookRepository.save(new Book("리팩토링", "마틴 파울ㄹ러", "333" , 35000));

        // when
        List<Book> books = bookRepository.searchBooks("로버트 마틴", null, null);
        // then
        assertThat(books).hasSize(1);
    }

    @Test
    @DisplayName("저자만 지정하여 검색한다")
    void searchByPriceRangeOnly() {
        // given
        bookRepository.save(new Book("클린 아키텍처", "로버트 마틴", "222" , 32000));
        bookRepository.save(new Book("리팩토링", "마틴 파울ㄹ러", "333" , 35000));

        // when
        List<Book> books = bookRepository.searchBooks(null, 25000, 35000);
        // then
        assertThat(books).hasSize(2);
    }

    @Test
    @DisplayName("조건을 지정하지 않으면 모든 책을 조회한다.")
    void searchAllIfNotCondition() {
        // given
        bookRepository.save(new Book("클린 아키텍처", "로버트 마틴", "222" , 32000));
        bookRepository.save(new Book("리팩토링", "마틴 파울러", "333" , 35000));
        bookRepository.save(new Book("리팩토링2", "마틴 파울러", "334[" , 35000));

        // when
        List<Book> books = bookRepository.searchBooks(null, null, null);
        // then
        assertThat(books).hasSize(3);
    }
}