package org.example.baitapcnpm.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.baitapcnpm.model.*;
import org.example.baitapcnpm.repository.CandidateCvRepository;
import org.example.baitapcnpm.repository.JobApplicationRepository;
import org.example.baitapcnpm.repository.JobPostingRepository;
import org.example.baitapcnpm.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CandidateCvRepository cvRepository;
    private final JobPostingRepository jobRepository;
    private final JobApplicationRepository applicationRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Dữ liệu đã tồn tại, bỏ qua DataSeeder.");
            return;
        }

        log.info("Bắt đầu khởi tạo dữ liệu mẫu cho hệ thống tuyển dụng...");

        // 1. Khởi tạo tài khoản
        User admin = userRepository.save(User.builder()
                .username("admin")
                .password("123456")
                .fullName("Quản Trị Viên Hệ Thống")
                .email("admin@recruitment.vn")
                .phone("0901234567")
                .role(Role.ROLE_ADMIN)
                .build());

        User recruiter1 = userRepository.save(User.builder()
                .username("fpt_hr")
                .password("123456")
                .fullName("Lê Thu Hà (HR Manager)")
                .email("recruiter@fpt-software.com")
                .phone("0912345678")
                .role(Role.ROLE_RECRUITER)
                .build());

        User recruiter2 = userRepository.save(User.builder()
                .username("viettel_hr")
                .password("123456")
                .fullName("Nguyễn Minh Quân (Talent Acquisition)")
                .email("hr@viettel.com.vn")
                .phone("0988776655")
                .role(Role.ROLE_RECRUITER)
                .build());

        User candidate1 = userRepository.save(User.builder()
                .username("candidate_nam")
                .password("123456")
                .fullName("Nguyễn Văn Nam")
                .email("nam.nguyen@gmail.com")
                .phone("0977112233")
                .role(Role.ROLE_CANDIDATE)
                .build());

        User candidate2 = userRepository.save(User.builder()
                .username("candidate_hoa")
                .password("123456")
                .fullName("Trần Thị Hoa")
                .email("hoa.tran@gmail.com")
                .phone("0966445566")
                .role(Role.ROLE_CANDIDATE)
                .build());

        // 2. Khởi tạo CV mẫu cho Ứng viên
        CandidateCv cv1 = cvRepository.save(CandidateCv.builder()
                .candidate(candidate1)
                .title("CV Lập trình viên Java Backend - Nguyễn Văn Nam")
                .summary("Lập trình viên nhiệt huyết với hơn 2 năm kinh nghiệm thực chiến với hệ sinh thái Java, Spring Boot, Microservices và cơ sở dữ liệu quan hệ SQL.")
                .skills("Java 17/21, Spring Boot, Spring Data JPA, MySQL, Docker, RESTful API, Git, Redis")
                .education("Cử nhân Công nghệ Thông tin - Đại học Bách Khoa Hà Nội (Tốt nghiệp loại Giỏi)")
                .experience("2023 - Nay: Java Developer tại Công ty Tech Solutions (Xây dựng API, xử lý tối ưu truy vấn CSDL, tích hợp cổng thanh toán).")
                .portfolioUrl("https://github.com/nguyenvannam-dev")
                .build());

        CandidateCv cv2 = cvRepository.save(CandidateCv.builder()
                .candidate(candidate2)
                .title("CV Frontend Developer / UI-UX - Trần Thị Hoa")
                .summary("Frontend Developer có niềm đam mê thiết kế trải nghiệm người dùng mượt mà, tối ưu responsive trên mọi thiết bị.")
                .skills("HTML5, CSS3, JavaScript/TypeScript, React.js, Next.js, Bootstrap, TailwindCSS")
                .education("Kỹ sư Phần mềm - Đại học Công nghệ (ĐHQGHN)")
                .experience("2022 - Nay: Frontend Developer tại Digital Agency (Phát triển giao diện web portal cho khách hàng quốc tế).")
                .portfolioUrl("https://github.com/hoatran-fe")
                .build());

        // 3. Khởi tạo Tin tuyển dụng
        JobPosting job1 = jobRepository.save(JobPosting.builder()
                .recruiter(recruiter1)
                .title("Senior / Middle Java Backend Engineer (Spring Boot)")
                .companyName("FPT Software")
                .location("Hà Nội (Duy Tân, Cầu Giấy)")
                .salaryRange("25,000,000 - 40,000,000 VNĐ")
                .employmentType("Toàn thời gian")
                .description("Tham gia phát triển các hệ thống Fintech, E-Commerce quy mô lớn phục vụ hàng triệu người dùng. Thiết kế kiến trúc module hóa và dịch vụ đám mây AWS.")
                .requirements("- Tối thiểu 2 năm kinh nghiệm với Java & Spring Boot.\n- Nắm vững kiến thức về OOP, Design Patterns, SOLID.\n- Có kinh nghiệm với RDBMS (MySQL, PostgreSQL) và NoSQL (Redis).\n- Khả năng đọc hiểu tài liệu tiếng Anh tốt.")
                .benefits("- Thưởng tháng lương 13 + thưởng hiệu quả dự án.\n- Bảo hiểm FPT Care cho nhân viên và gia đình.\n- Môi trường làm việc năng động, lộ trình thăng tiến rõ ràng.")
                .status(JobStatus.APPROVED)
                .build());

        JobPosting job2 = jobRepository.save(JobPosting.builder()
                .recruiter(recruiter1)
                .title("Chuyên Viên Kiểm Thử Phần Mềm (QA/QC Manual & Automation)")
                .companyName("FPT Software")
                .location("Đà Nẵng")
                .salaryRange("15,000,000 - 25,000,000 VNĐ")
                .employmentType("Toàn thời gian")
                .description("Lập test plan, test case, thực thi kiểm thử chức năng và phi chức năng. Phối hợp với Dev Team để tái hiện và quản lý bug.")
                .requirements("- 1-2 năm kinh nghiệm QA/Tester.\n- Nắm vững quy trình kiểm thử phần mềm (STLC/SDLC).\n- Biết dùng Selenium hoặc Postman là một lợi thế.")
                .benefits("- Đào tạo chứng chỉ ISTQB miễn phí.\n- Du lịch hàng năm, teambuilding sôi nổi.")
                .status(JobStatus.APPROVED)
                .build());

        JobPosting job3 = jobRepository.save(JobPosting.builder()
                .recruiter(recruiter2)
                .title("Frontend Developer (React.js / Next.js)")
                .companyName("Viettel Telecom")
                .location("Hà Nội (Keangnam Landmark 72)")
                .salaryRange("20,000,000 - 35,000,000 VNĐ")
                .employmentType("Toàn thời gian")
                .description("Phát triển các ứng dụng Web Portal phục vụ khách hàng doanh nghiệp và người dùng cá nhân của Tập đoàn Viettel.")
                .requirements("- Có kinh nghiệm làm việc với ReactJS, Redux/Zustand, TailwindCSS.\n- Hiểu biết về Web Vitals và SEO là một lợi thế lớn.")
                .benefits("- Thu nhập cạnh tranh hàng đầu thị trường viễn thông.\n- Chế độ đãi ngộ đặc biệt của Tập đoàn Viettel.")
                .status(JobStatus.APPROVED)
                .build());

        JobPosting job4 = jobRepository.save(JobPosting.builder()
                .recruiter(recruiter2)
                .title("Thực tập sinh Lập trình (Fresher / Intern Developer)")
                .companyName("Viettel Telecom")
                .location("Hà Nội / TP. Hồ Chí Minh")
                .salaryRange("8,000,000 - 12,000,000 VNĐ")
                .employmentType("Thực tập")
                .description("Chương trình ươm mầm tài năng trẻ ngành CNTT. Được kèm cặp 1-1 bởi các chuyên gia công nghệ hàng đầu.")
                .requirements("- Sinh viên năm cuối hoặc mới tốt nghiệp chuyên ngành CNTT, ATTT, ĐTVT.\n- Có kiến thức cơ bản về lập trình Java, Python hoặc JavaScript.")
                .benefits("- Hỗ trợ lương thực tập hấp dẫn.\n- Cơ hội lên nhân viên chính thức sau 3 tháng.")
                .status(JobStatus.PENDING_APPROVAL) // Tin đang chờ admin duyệt để demo tính năng Admin duyệt tin
                .build());

        // 4. Khởi tạo Đơn ứng tuyển mẫu để demo việc theo dõi trạng thái
        applicationRepository.save(JobApplication.builder()
                .job(job1)
                .candidate(candidate1)
                .cv(cv1)
                .coverLetter("Chào anh/chị tuyển dụng, em đã theo dõi FPT Software từ lâu và rất hào hứng với vị trí Backend Engineer. Với kinh nghiệm làm việc vững vàng về Spring Boot và Microservices, em tin mình sẽ đóng góp tốt cho dự án!")
                .status(ApplicationStatus.REVIEWING)
                .appliedAt(LocalDateTime.now().minusDays(2))
                .recruiterNotes("Hồ sơ ứng viên rất phù hợp với yêu cầu dự án. Dự kiến hẹn phỏng vấn kỹ thuật vào thứ 5 tuần này.")
                .build());

        log.info("Khởi tạo dữ liệu mẫu thành công!");
    }
}
