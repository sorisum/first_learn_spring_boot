package com.community.rest.controller;

import com.community.rest.domain.Board;
import com.community.rest.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/boards")
public class BoardRestController {

    private BoardRepository boardRepository;

    // @Autowire와 똑같이 의존성을 주입하는 생성자 주입 방식임
    public BoardRestController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    // Get으로 '/api/boards' 호출 시 해당 메서드에 매핑됨
    // 반환값은 JSON 타입임
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBoards(@PageableDefault Pageable pageable) {
        Page<Board> boards = boardRepository.findAll(pageable);
        // 현재 페이지 수, 총 게시판 수, 한 페이지의 게시판 수 등 페이징 처리에 관한 리소스를 만드는 PagedResources 객체를 생성하기 위해
        // PagedResources 생성자의 파라미터로 사용되는 PageMetadata 객체를 생성했음
        // PageMetadata는 전체 페이지 수, 현재 페이지 번호, 총 게시판 수로 구성됨
        PageMetadata pageMetadata = new PageMetadata(pageable.getPageSize(), boards.getNumber(), boards.getTotalElements());
        // PagedResource 객체를 생성함
        // 이 객체를 생성하면 HATEOAS가 적용되며 페이징값까지 생성된 REST형의 데이터를 만들어준다
        PagedResources<Board> resources = new PagedResources<>(boards.getContent(), pageMetadata);
        // PagedResources 객체 생성 시 따로 링크를 설정하지 않았다면 이와 같이 링크를 추가할 수 있다
        // 여기서는 각 Board마다 상세정보를 불러올 수 있는 링크만 추가했음
        resources.add(linkTo(methodOn(BoardRestController.class).getBoards(pageable)).withSelfRel());
        return ResponseEntity.ok(resources);
    }

}