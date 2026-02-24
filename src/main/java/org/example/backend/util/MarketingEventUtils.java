package org.example.backend.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.backend.dto.response.SellerMarketingOverviewResponse;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MarketingEventUtils {

    @Data
    @AllArgsConstructor
    static class EventRule {
        String title;
        String description;
        String actionLabel;
        String actionUrl;
        // 0 = Fixed Date, 1 = Double Day (e.g. 9.9), 2 = Black Friday, 3 = Lunar
        // (Hardcoded for now or approx)
        int type;
        int day;
        int month;
    }

    private static final List<EventRule> RULES = new ArrayList<>();

    static {
        // Fixed Holidays
        RULES.add(new EventRule("Tết Dương Lịch", "Khởi đầu năm mới bùng nổ doanh số.", "Tạo Deal",
                "/seller/marketing/deals", 0, 1, 1));
        RULES.add(new EventRule("Valentine 14/2", "Cơ hội vàng cho quà tặng và thời trang.", "Tạo mã giảm giá",
                "/seller/marketing/vouchers", 0, 14, 2));
        RULES.add(new EventRule("Quốc Tế Phụ Nữ 8/3", "Nhu cầu mua sắm quà tặng tăng cao.", "Chuẩn bị hàng",
                "/seller/products", 0, 8, 3));
        RULES.add(new EventRule("Đại Lễ 30/4 - 1/5", "Kỳ nghỉ dài, nhu cầu du lịch & mua sắm tăng.", "Tạo Flash Sale",
                "/seller/marketing/flash-sale", 0, 30, 4));
        RULES.add(new EventRule("Quốc Tế Thiếu Nhi 1/6", "Đẩy mạnh các sản phẩm cho mẹ và bé.", "Tạo Deal",
                "/seller/marketing/deals", 0, 1, 6));
        RULES.add(new EventRule("Quốc Khánh 2/9", "Lượng truy cập dự kiến tăng 300%. Hãy chuẩn bị hàng tồn kho.",
                "Tạo Deal Ngay", "/seller/marketing/deals", 0, 2, 9));
        RULES.add(new EventRule("Phụ Nữ Việt Nam 20/10", "Dịp mua sắm lớn cho phái đẹp.", "Tạo mã giảm giá",
                "/seller/marketing/vouchers", 0, 20, 10));
        RULES.add(new EventRule("Nhà Giáo Việt Nam 20/11", "Quà tặng tri ân thầy cô.", "Tạo Deal",
                "/seller/marketing/deals", 0, 20, 11));
        RULES.add(new EventRule("Giáng Sinh", "Mùa lễ hội cuối năm sôi động.", "Trang trí Shop", "/seller/settings", 0,
                24, 12));

        // Double Days
        RULES.add(new EventRule("Siêu Sale 1.1", "Mở màn năm mới.", "Đăng ký", "/seller/marketing/register", 1, 1, 1));
        RULES.add(
                new EventRule("Siêu Sale 2.2", "Săn sale đầu năm.", "Đăng ký", "/seller/marketing/register", 1, 2, 2));
        RULES.add(new EventRule("Siêu Sale 3.3", "Lễ hội mua sắm.", "Đăng ký", "/seller/marketing/register", 1, 3, 3));
        RULES.add(new EventRule("Siêu Sale 4.4", "Sale to bùng nổ.", "Đăng ký", "/seller/marketing/register", 1, 4, 4));
        RULES.add(new EventRule("Siêu Sale 5.5", "Chào hè rực rỡ.", "Đăng ký", "/seller/marketing/register", 1, 5, 5));
        RULES.add(new EventRule("Siêu Sale 6.6", "Sale giữa năm.", "Đăng ký", "/seller/marketing/register", 1, 6, 6));
        RULES.add(
                new EventRule("Siêu Sale 7.7", "Siêu hội hoàn xu.", "Đăng ký", "/seller/marketing/register", 1, 7, 7));
        RULES.add(
                new EventRule("Siêu Sale 8.8", "Sale to nhất năm.", "Đăng ký", "/seller/marketing/register", 1, 8, 8));
        RULES.add(new EventRule("Siêu Sale 9.9", "Ngày hội mua sắm lớn nhất tháng. Đừng bỏ lỡ.", "Đăng ký",
                "/seller/marketing/register", 1, 9, 9));
        RULES.add(new EventRule("Siêu Sale 10.10", "Săn sale thương hiệu.", "Đăng ký", "/seller/marketing/register", 1,
                10, 10));
        RULES.add(new EventRule("Siêu Sale 11.11", "Độc thân không cô đơn, chốt đơn mỏi tay.", "Đăng ký",
                "/seller/marketing/register", 1, 11, 11));
        RULES.add(new EventRule("Siêu Sale 12.12", "Sinh nhật NgayLe.com.", "Đăng ký", "/seller/marketing/register", 1,
                12, 12));
    }

    public static List<SellerMarketingOverviewResponse.UpcomingEvent> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        List<SellerMarketingOverviewResponse.UpcomingEvent> events = new ArrayList<>();

        // 1. Scan Fixed & Double Days for next 60 days
        for (EventRule rule : RULES) {
            LocalDate eventDate = LocalDate.of(today.getYear(), rule.month, rule.day);
            if (eventDate.isBefore(today)) {
                // Check if next year's date is within range (e.g. today is Dec 30, looking for
                // Jan 1)
                eventDate = eventDate.plusYears(1);
            }

            if (!eventDate.isBefore(today) && eventDate.isBefore(today.plusDays(60))) {
                events.add(mapToDto(rule, eventDate));
            }
        }

        // 2. Black Friday (Last Friday of Nov)
        LocalDate blackFriday = LocalDate.of(today.getYear(), 11, 1)
                .with(TemporalAdjusters.lastInMonth(DayOfWeek.FRIDAY));
        if (blackFriday.isBefore(today))
            blackFriday = blackFriday.plusYears(1);
        if (!blackFriday.isBefore(today) && blackFriday.isBefore(today.plusDays(60))) {
            events.add(SellerMarketingOverviewResponse.UpcomingEvent.builder()
                    .date(String.format("%02d", blackFriday.getDayOfMonth()))
                    .month("Th11")
                    .title("Black Friday")
                    .description("Lễ hội mua sắm lớn nhất thế giới.")
                    .actionLabel("Sale Sập Sàn")
                    .actionUrl("/seller/marketing/campaigns")
                    .build());
        }

        // Sort by date
        events.sort(Comparator.comparing(
                e -> LocalDate.of(LocalDate.now().getYear(), parseMonth(e.getMonth()), Integer.parseInt(e.getDate()))));

        // Return top 2-3
        return events.stream().limit(3).collect(Collectors.toList());
    }

    private static int parseMonth(String monthStr) {
        // "Th09" -> 9
        return Integer.parseInt(monthStr.replace("Th", ""));
    }

    private static SellerMarketingOverviewResponse.UpcomingEvent mapToDto(EventRule rule, LocalDate date) {
        return SellerMarketingOverviewResponse.UpcomingEvent.builder()
                .date(String.format("%02d", date.getDayOfMonth()))
                .month("Th" + String.format("%02d", date.getMonthValue()))
                .title(rule.title)
                .description(rule.description)
                .actionLabel(rule.actionLabel)
                .actionUrl(rule.actionUrl)
                .build();
    }
}
