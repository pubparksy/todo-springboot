package com.example.demo.controller;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.TodoDTO;
import com.example.demo.model.TodoEntity;
import com.example.demo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("todo")
public class TodoController {

	@Autowired
	private TodoService service;

	@GetMapping("/test")
	public ResponseEntity<?> testTodo() {
		String str = service.testService(); // 테스트 서비스 사용
		List<String> list = new ArrayList<>();
		list.add(str);
		ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
		// ResponseEntity.ok(response) 를 사용해도 상관 없음
		return ResponseEntity.ok().body(response);
	}

	@PostMapping
	public ResponseEntity<?> createTodo(@RequestBody TodoDTO dto) {
		try {
			String temporaryUserId = "temporary-user"; // 임시 사용자 아이디

			//(1) 매개변수로 받아온 RequestBody TodoDTO를 > TodoEntity로 변환한다.
			TodoEntity entity = TodoDTO.toEntity(dto); // TodoDTO에 만든 toEntity 메서드

			//(2) id를 null로 초기화한다. 새롭게 생성할 땐 id가 없어야하니까
			entity.setId(null); // null로 직접 초기화하지 않으면 null이지만 ox00 뭔가로 저장되어있음?

			//(3) 임시 userId 지정. 나중에 4장에서 수정할거라 지금은 '임시 유저'로만 로긴없이 사용가능한 앱인 상태.
			entity.setUserId(temporaryUserId);

			//(4) Service의 create 메서드로 새로운 Todo 엔티티 생성.
			List<TodoEntity> entities = service.create(entity);

			//(5) create했으니 (Stream으로) List<TodoEntity>를 > List<TodoDTO>로 변환한다.
			List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

			//(6) List<TodoDTO>를 > (Response Error 메시지도 담는) ResponseDTO<TodoDTO>로 변환한다.
			ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

			//(7) 클라한테 ResponseDTO를 반환한다.
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			//(8) 예외 발생하면 dto대신 error에 message 넣어서 반환
			ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder()
					.error(e.getMessage()).build();
			return ResponseEntity.badRequest().body(response);
		}
	}

	@GetMapping
	public ResponseEntity<?> retrieveTodoList() {
		String temporaryUserId = "temporary-user"; // 임시 아이디
		
		//(1) Service.java의 retrieve 메서드 사용
		List<TodoEntity> entities = service.retrieve(temporaryUserId);
		//(2) Stream으로 TodoEntity를 > Error Message도 담는 TodoDTO로 변환
		List<TodoDTO> dtos = entities.stream().map(TodoDTO::new)
				.collect(Collectors.toList());
		//(3) TodoDTO를 > ResponseDTO로 변환
		ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
		//(4) 클라에 return ResponseDTO
		return ResponseEntity.ok().body(response);
	}

	@PutMapping
	public ResponseEntity<?> updateTodo(@RequestBody TodoDTO dto) {
		String temporaryUserId = "temporary-user"; // 임시 아이디
		//(1) TodoDTO를 > TodoEntity로 변환
		TodoEntity entity = TodoDTO.toEntity(dto);
		//(2) id를 temporaryUserId로 초기화.
		entity.setUserId(temporaryUserId);
		//(3) Service의 update 메서드 사용해서 수정
		List<TodoEntity> entities = service.update(entity);
		//(4) Stream으로 TodoEntity를 > Error Msg까지 담는 TodoDTO로 변환
		List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
		//(5) TodoDTO를 > ResponseDTO로 변환
		ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
		//(6) 클라에 Response하고 ResponseDTO 반환
		return ResponseEntity.ok().body(response);
	}

	@DeleteMapping
	public ResponseEntity<?> deleteTodo(@RequestBody TodoDTO dto) {
		try {
			String temporaryUserId = "temporary-user"; // 임시 아이디
			//(1) TodoDTO를 > TodoEntity로 변환
			TodoEntity entity = TodoDTO.toEntity(dto);
			//(2) 임시 userId 지정. 나중에 4장에서 수정할거라 지금은 '임시 유저'로만 로긴없이 사용가능한 앱인 상태.
			entity.setUserId(temporaryUserId);
			//(3) Service.delete 메서드로 entity 삭제
			List<TodoEntity> entities = service.delete(entity);
			//(4) Stream으로 TodoEntity를 > Error Msg까지 담는 TodoDTO로 변환
			List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
			//(5) TodoDTO를 > ResponseDTO로 변환
			ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
			//(6) 클라에 Response하고 ResponseDTO 반환
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			//(7) 예외 발생 시, dto 대신 error 넣어서 return
			ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(e.getMessage()).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
}
