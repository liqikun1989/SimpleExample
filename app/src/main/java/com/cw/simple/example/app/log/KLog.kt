package com.cw.simple.example.app.log

import android.app.Application
import com.cw.simple.example.app.utils.FileUtils
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.ClassicFlattener
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.ConsolePrinter
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy2
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import java.io.File

class KLog {
    companion object {
        private var enableGlobalBorder = false
        fun init(application: Application, globalTag: String, enableLog: Boolean, writeToFile: Boolean, enableBorder: Boolean = false) {
            enableGlobalBorder = enableBorder
            val logConfigBuilder = LogConfiguration.Builder()
                .logLevel(LogLevel.ALL)
                .tag(globalTag)
                .disableThreadInfo()
                .borderFormatter(KBorderFormatter())

            if (enableGlobalBorder) {
                logConfigBuilder.enableBorder()
            } else {
                logConfigBuilder.disableBorder()
            }

            var androidPrinter: AndroidPrinter? = null
            var consolePrinter: ConsolePrinter? = null

            var filePrinter: FilePrinter? = null

            if (writeToFile) {
                val logFolder = File(FileUtils.getExternalStorageDirectory(application).toString() + "/Android/data/" + application.packageName, "klog")
                if (!logFolder.exists()) {
                    logFolder.mkdirs()
                }
                filePrinter = FilePrinter.Builder(logFolder.absolutePath) // 指定保存日志文件的路径
                    .fileNameGenerator(DateFileNameGenerator()) // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
                    .backupStrategy(FileSizeBackupStrategy2(5 * 1024 * 1024, 5)) // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
                    .flattener(ClassicFlattener())
                    .build()
            }

            if (enableLog) {
                androidPrinter = AndroidPrinter(true)
//                consolePrinter = ConsolePrinter()
            }

            val printers = mutableListOf<Printer>()
            if (androidPrinter != null) {
                printers.add(androidPrinter)
            }
            if (consolePrinter != null) {
                printers.add(consolePrinter)
            }
            if (filePrinter != null) {
                printers.add(filePrinter)
            }
            if (printers.isNotEmpty()) {
                XLog.init(logConfigBuilder.build(), *printers.toTypedArray())
            } else {
                XLog.init(logConfigBuilder.build())
            }
        }

        @JvmStatic @JvmOverloads
        fun d(tag: String, content: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.tag(tag).enableBorder().d(content, tr)
            } else {
                if (enableGlobalBorder) {
                    XLog.tag(tag).enableBorder().d(content, tr)
                } else {
                    XLog.tag(tag).disableBorder().d(content, tr)
                }
            }
        }

        @JvmStatic @JvmOverloads
        fun d(content: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.enableBorder().d(content, tr)
            } else {
                if (enableGlobalBorder) {
                    XLog.enableBorder().d(content, tr)
                } else {
                    XLog.disableBorder().d(content, tr)
                }
            }
        }

        @JvmStatic @JvmOverloads
        fun d(any: Any, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.enableBorder().d(any)
            } else {
                if (enableGlobalBorder) {
                    XLog.enableBorder().d(any)
                } else {
                    XLog.disableBorder().d(any)
                }
            }
        }

        @JvmStatic @JvmOverloads
        fun d(array: Array<Any>, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.enableBorder().d(array)
            } else {
                if (enableGlobalBorder) {
                    XLog.enableBorder().d(array)
                } else {
                    XLog.disableBorder().d(array)
                }
            }
        }

        @JvmStatic @JvmOverloads
        fun i(tag: String, content: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.enableBorder().tag(tag).i(content, tr)
            } else {
                if (enableGlobalBorder) {
                    XLog.enableBorder().tag(tag).i(content, tr)
                } else {
                    XLog.disableBorder().tag(tag).i(content, tr)
                }
            }
        }

        @JvmStatic @JvmOverloads
        fun i(content: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.enableBorder().i(content, tr)
            } else {
                if (enableGlobalBorder) {
                    XLog.enableBorder().i(content, tr)
                } else {
                    XLog.disableBorder().i(content, tr)
                }
            }
        }

        @JvmStatic @JvmOverloads
        fun i(any: Any, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.enableBorder().i(any)
            } else {
                if (enableGlobalBorder) {
                    XLog.enableBorder().i(any)
                } else {
                    XLog.disableBorder().i(any)
                }
            }
        }

        @JvmStatic @JvmOverloads
        fun i(array: Array<Any>, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.enableBorder().i(array)
            } else {
                if (enableGlobalBorder) {
                    XLog.enableBorder().i(array)
                } else {
                    XLog.disableBorder().i(array)
                }
            }
        }

        @JvmStatic @JvmOverloads
        fun w(tag: String, content: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.enableBorder().tag(tag).w(content, tr)
            } else {
                if (enableGlobalBorder) {
                    XLog.enableBorder().tag(tag).w(content, tr)
                } else {
                    XLog.disableBorder().tag(tag).w(content, tr)
                }
            }
        }

        @JvmStatic @JvmOverloads
        fun w(content: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.enableBorder().w(content, tr)
            } else {
                if (enableGlobalBorder) {
                    XLog.enableBorder().w(content, tr)
                } else {
                    XLog.disableBorder().w(content, tr)
                }
            }
        }

        @JvmStatic
        fun w(any: Any, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.enableBorder().w(any)
            } else {
                if (enableGlobalBorder) {
                    XLog.enableBorder().w(any)
                } else {
                    XLog.disableBorder().w(any)
                }
            }
        }

        @JvmStatic
        fun w(array: Array<Any>, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.enableBorder().w(array)
            } else {
                if (enableGlobalBorder) {
                    XLog.enableBorder().w(array)
                } else {
                    XLog.disableBorder().w(array)
                }
            }
        }

        @JvmStatic @JvmOverloads
        fun e(tag: String, content: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.enableBorder().tag(tag).e(content, tr)
            } else {
                if (enableGlobalBorder) {
                    XLog.enableBorder().tag(tag).e(content, tr)
                } else {
                    XLog.disableBorder().tag(tag).e(content, tr)
                }
            }
        }

        @JvmStatic @JvmOverloads
        fun e(content: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.enableBorder().e(content, tr)
            } else {
                if (enableGlobalBorder) {
                    XLog.enableBorder().e(content, tr)
                } else {
                    XLog.disableBorder().e(content, tr)
                }
            }
        }

        @JvmStatic @JvmOverloads
        fun e(any: Any, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.enableBorder().e(any)
            } else {
                if (enableGlobalBorder) {
                    XLog.enableBorder().e(any)
                } else {
                    XLog.disableBorder().e(any)
                }
            }
        }

        @JvmStatic @JvmOverloads
        fun e(array: Array<Any>, enableBorder: Boolean = false) {
            if (enableBorder) {
                XLog.enableBorder().e(array)
            } else {
                if (enableGlobalBorder) {
                    XLog.enableBorder().e(array)
                } else {
                    XLog.disableBorder().e(array)
                }
            }
        }

        @JvmStatic
        fun json(tag: String, json: String) {
            XLog.enableBorder().tag(tag).json(json)
        }

        @JvmStatic
        fun json(json: String) {
            XLog.enableBorder().json(json)
        }

        @JvmStatic
        fun xml(tag: String, xml: String) {
            XLog.enableBorder().tag(tag).xml(xml)
        }

        @JvmStatic
        fun xml(xml: String) {
            XLog.enableBorder().json(xml)
        }
    }
}