package com.nodex.allcrew.mapper

import com.nodex.allcrew.domain.Member
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

/**
 * 회원 테이블(`members`) MyBatis Mapper 인터페이스.
 *
 * 실제 SQL은 `resources/mapper/MemberMapper.xml`에 정의되어 있으며,
 * 이 인터페이스의 메서드명과 XML의 `id`가 일치해야 한다.
 */
@Mapper
interface MemberMapper {

    /** 전체 회원 목록 조회 (id 오름차순) */
    fun findAll(): List<Member>

    /**
     * PK로 회원 단건 조회.
     *
     * @param id 회원 PK
     * @return 존재하면 Member, 없으면 null
     */
    fun findById(@Param("id") id: Long): Member?

    /**
     * 이메일로 회원 조회.
     *
     * 중복 이메일 검증 시 사용한다.
     *
     * @param email 조회할 이메일
     * @return 존재하면 Member, 없으면 null
     */
    fun findByEmail(@Param("email") email: String): Member?

    /**
     * 신규 회원 등록.
     *
     * insert 후 `useGeneratedKeys`로 [Member.id]에 자동 생성된 PK가 채워진다.
     *
     * @param member 등록할 회원 (id는 무시됨)
     * @return 영향받은 행 수
     */
    fun insert(member: Member): Int

    /**
     * 회원 정보 수정.
     *
     * @param member 수정할 회원 (id 필수)
     * @return 영향받은 행 수
     */
    fun update(member: Member): Int

    /**
     * PK로 회원 삭제.
     *
     * @param id 삭제할 회원 PK
     * @return 영향받은 행 수 (없으면 0)
     */
    fun deleteById(@Param("id") id: Long): Int
}
