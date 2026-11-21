/**
 * 系统管理与安全层（对应 docs/01_后端架构设计.md §5.3 §6）。
 *
 * <p>规划子包：config（SecurityConfig / MybatisPlusConfig）、security（JwtTokenProvider /
 * JwtAuthenticationFilter / LoginUser / DataScopeContext / DataScopeInterceptor）、
 * controller.admin、service、service.impl、mapper、entity。</p>
 */
package com.stable.angina.system;
