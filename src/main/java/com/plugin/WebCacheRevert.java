package com.plugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Properties;

/**
 * @Author: xiaoxiang.zhang
 * @Description: 前端文件名称恢复
 * @Date: Create in 5:40 PM 2019/10/27
 */
@Mojo(name = "webCacheRevert", defaultPhase = LifecyclePhase.PACKAGE)
public class WebCacheRevert extends AbstractMojo {

    @Parameter
    private String baseDir;

    @Parameter
    private String propertiesPath;

    @Parameter
    private String suffixs;

    /**
     * 默认配置文件路径
     */
    private final String DEFAULT_PROPERTIES_PATH = "default.properties";

    /**
     * 默认后缀键
     */
    private final String DEFAULT_SUFF_KEY = "defaultSuffix";

    public void execute() throws MojoExecutionException, MojoFailureException {
        //1.传入参数校验
        if (StringUtils.isBlank(baseDir)) {
            throw new MojoFailureException("There is no parameter that is named baseDir");
        }
        if (StringUtils.isBlank(suffixs)) {
            try {
                if (StringUtils.isBlank(propertiesPath)) {
                    propertiesPath = DEFAULT_PROPERTIES_PATH;
                }
                Properties defaultProperties = new Properties();
                defaultProperties.load(WebCacheRevert.class.getClassLoader().getResourceAsStream(propertiesPath));
                if (StringUtils.isBlank(defaultProperties.getProperty(DEFAULT_SUFF_KEY))) {
                    //若传入的配置文件中不存在改配置，那么则使用插件本省的默认配置
                    defaultProperties.load(WebCacheRevert.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_PATH));
                }
                suffixs = defaultProperties.getProperty(DEFAULT_SUFF_KEY);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //2.扫描指定目录并将文件名恢复
        scanFile(baseDir);
    }

    /*
     * @Author xiangxz
     * @Description
     * @Date 5:59 PM 2019/10/27
     * @Param java.lang.String
     * @return void
     **/

    private void scanFile(String baseDir) {
        //1.拿到当前根目录
        File baseFile = new File(baseDir);
        //2.判断是否为目录
        if (baseFile.isDirectory()) {
            File[] childFiles = baseFile.listFiles();
            for (File childFile : childFiles) {
                if (childFile.getPath().contains("target")) {
                    continue;
                }
                if (childFile.isDirectory()) {
                    //2.1若为目录，则进行递归。
                    scanFile(childFile.getPath());
                } else {
                    //2.3若为文件，则对文件进行处理
                    String fullFileName = childFile.getName();
                    int dotIndex = fullFileName.indexOf(".");
                    String suffix = fullFileName.substring(dotIndex);
                    //2.4若后缀包含在预处理文件中，则进行处理
                    if (suffixs.contains(suffix)) {
                        int seperaterIndex = fullFileName.indexOf("-");
                        String orignalFileName = fullFileName.substring(0, seperaterIndex);
                        String newFileName = childFile.getParentFile().getPath() + File.separator + orignalFileName + suffix;
                        childFile.renameTo(new File(newFileName));
                    }
                }
            }
        }
    }
}
