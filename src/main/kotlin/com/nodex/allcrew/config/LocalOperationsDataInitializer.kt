package com.nodex.allcrew.config

import com.nodex.allcrew.mapper.AdminMemberMapper
import com.nodex.allcrew.mapper.AdminOperationsMapper
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
@Profile("local")
@Order(2)
class LocalOperationsDataInitializer(
    private val adminOperationsMapper: AdminOperationsMapper,
    private val adminMemberMapper: AdminMemberMapper,
    private val jdbcTemplate: JdbcTemplate,
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        if (adminOperationsMapper.countCrewByAgencyId(AGENCY_ID) > 0) {
            return
        }

        val member = adminMemberMapper.findByEmail("admin@allcrew.com") ?: return
        val memberId = member.id!!

        seedProjects(memberId)
        seedCrewAndLinks()
        seedContracts()
        seedSettlements()
        seedChatRooms()
        seedIncidents()
        seedActivities()
    }

    private fun seedProjects(memberId: Long) {
        jdbcTemplate.update(
            """
            INSERT INTO admin_projects (
                agency_id, project_code, name, event_type, start_date, end_date,
                work_start_time, work_end_time, address, recruitment_deadline,
                status, crew_current, crew_total, budget, progress, created_by_member_id
            ) VALUES
            (?, 'PRJ-0007', '홍대 야간 행사', '공연·행사', DATE '2026-05-01', DATE '2026-06-30', TIME '18:00:00', TIME '23:00:00', '서울 마포구', DATE '2026-04-25', 'COMPLETED', 3, 5, 500000, 100, ?),
            (?, 'PRJ-0008', '강남 팝업 스태프', 'MICE·전시', DATE '2026-05-15', DATE '2026-06-30', TIME '10:00:00', TIME '19:00:00', '서울 강남구', DATE '2026-05-10', 'IN_PROGRESS', 4, 6, 720000, 60, ?),
            (?, 'PRJ-0009', '6/16 test', '기타', DATE '2026-06-01', DATE '2026-06-30', TIME '09:00:00', TIME '18:00:00', '서울 강서구', DATE '2026-05-28', 'IN_PROGRESS', 5, 8, 960000, 45, ?),
            (?, 'PRJ-0010', '신논현 치맥 페스티벌', '공연·행사', DATE '2026-06-10', DATE '2026-06-25', TIME '14:00:00', TIME '22:00:00', '서울 강남구', DATE '2026-06-05', 'IN_PROGRESS', 6, 10, 1200000, 70, ?),
            (?, 'PRJ-0012', 'asd', '기타', DATE '2026-06-01', DATE '2026-06-30', TIME '09:00:00', TIME '18:00:00', '서울 서초구', DATE '2026-05-25', 'RECRUITING', 3, 5, 600000, 30, ?)
            """.trimIndent(),
            AGENCY_ID, memberId,
            AGENCY_ID, memberId,
            AGENCY_ID, memberId,
            AGENCY_ID, memberId,
            AGENCY_ID, memberId,
        )
    }

    private fun seedCrewAndLinks() {
        jdbcTemplate.update(
            """
            INSERT INTO admin_crew_members (agency_id, crew_code, name, phone, role, project_count, work_days, recent_work_date, rating) VALUES
            (?, 'CRW-001', '김민수', '01011110012', '크루', 6, 6, DATE '2026-06-19', 5.0),
            (?, 'CRW-002', '이서연', '01022220034', '크루', 5, 5, DATE '2026-06-18', 4.9),
            (?, 'CRW-003', '박준호', '01033330056', '크루', 4, 4, DATE '2026-06-17', 4.8),
            (?, 'CRW-004', '최유진', '01044440078', '크루', 3, 3, DATE '2026-06-15', 5.0),
            (?, 'CRW-005', '정하늘', '01055550090', '크루', 2, 2, DATE '2026-06-12', 4.7)
            """.trimIndent(),
            AGENCY_ID, AGENCY_ID, AGENCY_ID, AGENCY_ID, AGENCY_ID,
        )

        val crewIdByCode = jdbcTemplate.query(
            "SELECT id, crew_code FROM admin_crew_members WHERE agency_id = ?",
            { rs, _ -> rs.getString("crew_code") to rs.getLong("id") },
            AGENCY_ID,
        ).toMap()

        val links = mapOf(
            "CRW-001" to listOf("PRJ-0010", "PRJ-0009", "PRJ-0012"),
            "CRW-002" to listOf("PRJ-0010", "PRJ-0008"),
            "CRW-003" to listOf("PRJ-0009", "PRJ-0007"),
            "CRW-004" to listOf("PRJ-0012", "PRJ-0008"),
            "CRW-005" to listOf("PRJ-0007"),
        )

        links.forEach { (code, projectCodes) ->
            val crewId = crewIdByCode[code] ?: return@forEach
            projectCodes.forEach { projectCode ->
                jdbcTemplate.update(
                    "INSERT INTO admin_crew_project_links (crew_id, project_code) VALUES (?, ?)",
                    crewId,
                    projectCode,
                )
            }
        }
    }

    private fun seedContracts() {
        jdbcTemplate.update(
            """
            INSERT INTO admin_contracts (agency_id, contract_code, crew_name, crew_role, project_name, project_code, contract_type, sent_date, signed_date, status) VALUES
            (?, 'CTR-0011', '박서준', '운영', 'asd', 'PRJ-0012', '표준 근로', DATE '2026-06-18', DATE '2026-06-18', 'SIGNED'),
            (?, 'CTR-0010', '김민수', '크루', '6/16 test', 'PRJ-0009', '표준 근로', DATE '2026-06-17', DATE '2026-06-17', 'SIGNED'),
            (?, 'CTR-0009', '이서연', '크루', '신논현 치맥 페스티벌', 'PRJ-0010', '표준 근로', DATE '2026-06-10', DATE '2026-06-11', 'SIGNED'),
            (?, 'CTR-0008', '최유진', '크루', 'asd', 'PRJ-0012', '표준 근로', DATE '2026-06-16', NULL, 'PENDING'),
            (?, 'CTR-0007', '정하늘', '크루', '6/16 test', 'PRJ-0009', '표준 근로', NULL, NULL, 'UNSIGNED'),
            (?, 'CTR-0006', '박준호', '크루', '강남 팝업 스태프', 'PRJ-0008', '표준 근로', DATE '2026-05-28', DATE '2026-05-28', 'SIGNED')
            """.trimIndent(),
            AGENCY_ID, AGENCY_ID, AGENCY_ID, AGENCY_ID, AGENCY_ID, AGENCY_ID,
        )
    }

    private fun seedSettlements() {
        jdbcTemplate.update(
            """
            INSERT INTO admin_settlements (agency_id, settlement_code, crew_name, crew_role, work_hours, hourly_rate, pre_tax, deduction, net_pay, status, project_code) VALUES
            (?, 'SET-0003', '김민수', '크루', 16, 12000, 192000, 6336, 185664, 'APPROVED', 'PRJ-0012'),
            (?, 'SET-0002', '이서연', '크루', 12, 11000, 132000, 4356, 127644, 'PAID', 'PRJ-0012'),
            (?, 'SET-0001', '박준호', '크루', 8, 10000, 80000, 2640, 77360, 'PAID', 'PRJ-0009'),
            (?, 'SET-0004', '최유진', '크루', 10, 12000, 120000, 3960, 116040, 'PENDING', 'PRJ-0012')
            """.trimIndent(),
            AGENCY_ID, AGENCY_ID, AGENCY_ID, AGENCY_ID,
        )
    }

    private fun seedChatRooms() {
        jdbcTemplate.update(
            """
            INSERT INTO admin_chat_rooms (agency_id, room_code, title, preview, room_time, room_type, project_code, avatar_text, avatar_color) VALUES
            (?, 'CHT-0011', '6월 12일 테스트 전체', '박서준 외 3명 · 메시지가 없어요', '16:30', 'PROJECT', 'PRJ-0009', '6', '#fa8c16'),
            (?, 'CHT-0010', '박서준 · 6/16 test', '1:1 · 메시지가 없어요', '06.15', 'DIRECT', 'PRJ-0009', '박', '#1677ff'),
            (?, 'CHT-0009', '신논현 치맥 페스티벌', '김민수 외 5명 · 메시지가 없어요', '06.12', 'PROJECT', 'PRJ-0010', '신', '#52c41a'),
            (?, 'CHT-0007', 'asd 전체', '최유진 외 2명 · 메시지가 없어요', '06.08', 'PROJECT', 'PRJ-0012', 'a', '#13c2c2')
            """.trimIndent(),
            AGENCY_ID, AGENCY_ID, AGENCY_ID, AGENCY_ID,
        )
    }

    private fun seedIncidents() {
        jdbcTemplate.update(
            """
            INSERT INTO admin_safenet_incidents (agency_id, incident_code, title, project_name, location, reporter, incident_time, status, project_code) VALUES
            (?, 'INC-0001', '미아 발생', 'Test', '덕은동 일대', '박서준', '16:20', 'CLOSED', 'PRJ-0009')
            """.trimIndent(),
            AGENCY_ID,
        )
    }

    private fun seedActivities() {
        jdbcTemplate.update(
            """
            INSERT INTO admin_activities (agency_id, content, activity_date) VALUES
            (?, '박서준님 정산 지급 완료', DATE '2026-06-19'),
            (?, '이민호님 정산 지급 완료', DATE '2026-06-17'),
            (?, '최유진님 정산 지급 완료', DATE '2026-06-17'),
            (?, '김지훈님 교육 이수 완료', DATE '2026-06-16'),
            (?, '정수아님 프로젝트 배정 완료', DATE '2026-06-15')
            """.trimIndent(),
            AGENCY_ID, AGENCY_ID, AGENCY_ID, AGENCY_ID, AGENCY_ID,
        )
    }

    companion object {
        private const val AGENCY_ID = 1L
    }
}
