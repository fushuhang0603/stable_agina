/**
 * 公共层（对应 docs/01_后端架构设计.md §5.3）。
 *
 * <p>规划子包：api（Result / ErrorCodes / PageResult）、exception、entity（BaseEntity）、
 * enums、util、sensitive（脱敏）、log（@OperationLog + AOP）、config（Redis / Jackson）。</p>
 *
 * <p>约束：本模块不得反向依赖 sa-system / sa-business。</p>
 */
package com.stable_angina.common;
