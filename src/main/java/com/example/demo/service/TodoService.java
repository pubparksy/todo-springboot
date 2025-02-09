
package com.example.demo.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.model.TodoEntity;
import com.example.demo.persistence.TodoRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TodoService {

	@Autowired
	private TodoRepository repository;

	public List<TodoEntity> create(final TodoEntity entity) {
		//Validations
		validate(entity);

		repository.save(entity);
		log.info("Entity Id : {} is saved.", entity.getId());

		return repository.findByUserId(entity.getUserId());
	}

	public List<TodoEntity> retrieve(final String userId) {
		return repository.findByUserId(userId);
	}

	public List<TodoEntity> update(final TodoEntity entity) {
		//(1) 매개변수로 넘어온 entity가 유효한지 확인
		validate(entity);
		//(2) 유효한 entity의 id로 적합한 TodoEntity를 DB에서 가져옴.
		final Optional<TodoEntity> original = repository.findById(entity.getId()); // class Optional로 null방지 Wrap

		//(3) Optional null이 아닐 경우의 isPreset 메소드 = Swift의 guardlet
		original.ifPresent(todo -> {
			// 임시로 새로운 TodoEntity 생성 [final TodoEntity td = original.get();]
			// (4) null이 아니면 새로운 객체에 set
			todo.setTitle(entity.getTitle());
			todo.setDone(entity.isDone());
			// (5) 수정된 객체를 DB에 저장
			repository.save(todo);
		});

		return retrieve(entity.getUserId());
	}

	public List<TodoEntity> delete(final TodoEntity entity) {
		//(1) 매개변수로 넘어온 entity가 유효한지 확인
		validate(entity);
		try {
			//(2) entity가 유효하면 삭제
			repository.delete(entity);
		} catch (Exception e) {
			//(3) execption 발생 시 id, exception을 로깅
			log.error("error deleting entity ", entity.getId(), e);
			//(4) JPA 함수 수행 시 DB 관련 exception이 발생하면 JPA가 DataAccessException 또는 기타 DB 관련 예외를 던질 수 있음.
			// 하지만 DB 내부 데이터베이스 구조가 외부에 노출될 수도 있어서 컨트롤러에 바로 직접 던지는 것은 위험할수 있음.
			// 그래서 DB 내부로직을 캡슐화하기 위해서 Service Layer에서 PresentationLayer에
			// e말고 새 exception을 리턴해서 '정확한 exception' 대신 'delete중 exception' 인지용으로 처리.
			throw new RuntimeException(e);
		}
		//(5) 새로운 TodoList를 가져와서 리턴한다.
		return retrieve(entity.getUserId());
	}

	// 리팩토링한 메서드
	private static void validate(TodoEntity entity) {
		if (entity == null) {
			log.warn("Entity cannot be null.");
			throw new RuntimeException("ENtity cannot be null");
		}

		if (entity.getUserId() == null) {
			log.warn("Unknown user.");
			throw new RuntimeException("Unknown user.");
		}
	}


	public String testService() {
		// TodoEntity 생성
		TodoEntity entity = TodoEntity.builder().title("My first todo item!").build();
		// TodoEntity 저장
		repository.save(entity);
		// TodoEntity 검색
		TodoEntity savedEntity = repository.findById(entity.getId()).get();
		return savedEntity.getTitle();
	}

}
