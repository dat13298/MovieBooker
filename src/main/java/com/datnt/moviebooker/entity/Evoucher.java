package com.datnt.moviebooker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "evoucher")
@Data
public class Evoucher extends BaseEntity {

    @Id
    @Column(length = 36)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String code;

    @Column(name = "evoucher_name", nullable = false, length = 255)
    private String evoucherName;

    @Column(name = "brand_name", length = 255)
    private String brandName;

    @Column(name = "points_required", nullable = false)
    private int pointsRequired;

    @Column(name = "expiry_date", nullable = false)
    private String expiryDate;

    @Column(name = "gift_id", length = 36, nullable = false)
    private String giftId;

    public enum Status {
        UNUSED, USED, EXPIRED, FAILED, CANCEL
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Status status;

    @Column(length = 20)
    private String serial;

    public enum TypeCode {
        BARCODE, QRCODE, TEXTCODE
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "type_code", length = 10)
    private TypeCode typeCode;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "ref_id", length = 50, nullable = false)
    private String refId;
}
