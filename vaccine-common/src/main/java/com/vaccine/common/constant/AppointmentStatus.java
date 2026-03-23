package com.vaccine.common.constant;

/**
 * 预约状态常量
 */
public class AppointmentStatus {

    /**
     * 未接种（已预约/审核通过）
     */
    public static final int PENDING = 0;

    /**
     * 已接种
     */
    public static final int VACCINATED = 1;

    /**
     * 已取消（包括审核拒绝）
     */
    public static final int CANCELLED = 2;

    /**
     * 待审核
     */
    public static final int PENDING_REVIEW = 3;
}