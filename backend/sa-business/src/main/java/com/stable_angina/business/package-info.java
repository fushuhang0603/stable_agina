/**
 * 业务层（对应 docs/01_后端架构设计.md §5.3）。
 *
 * <p>八个业务域：patient / monitor / medication / followup / ai / file / notice / stats，
 * 另有 job 定时任务。每个域内固定分层：controller.admin、controller.client、
 * service、service.impl、mapper、entity、dto、vo。</p>
 *
 * <p>MyBatis XML 统一放 resources/mapper/{domain}/ 下。</p>
 */
package com.stable_angina.business;
