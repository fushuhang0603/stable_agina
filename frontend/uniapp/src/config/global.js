/**
 * 全局静态资源配置
 * 对齐和仁 src/config/global.ts：BASE_IMG 为 OSS 图片目录，
 * 空态/占位等图片统一从该目录按文件名拼接。
 */
export const BASE_IMG =
  import.meta.env.VITE_BASE_IMG || 'https://phsdevoss.eheren.com/pcloud/phs3.0/'

export default { BASE_IMG }
