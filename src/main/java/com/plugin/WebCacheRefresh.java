package com.plugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/*
 * @Author xiaoxiang.zhang
 * @Description 前端文件缓存插件
 * @Date 10:51 AM 2019/10/26
 **/

@Mojo(name = "webCacheRefresh", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class WebCacheRefresh extends AbstractMojo {

    /**
     * 扩展名类型（即哪些类型的文件需要去刷新本地缓存）
     */
    @Parameter
    private String suffixs;

    @Parameter
    private String propertiesPath;

    @Parameter
    private String baseDir;

    /**
     * 默认配置文件路径
     */
    private final String DEFAULT_PROPERTIES_PATH = "default.properties";

    /**
     * 默认后缀键
     */
    private final String DEFAULT_SUFF_KEY = "defaultSuffix";

    public void execute() throws MojoExecutionException, MojoFailureException {
        //1.加载参数
        //1.1 校验是否传入拓展类型
        if (StringUtils.isBlank(suffixs)) {
            //1.1.1 校验是否传入配置文件路径
            if (StringUtils.isBlank(propertiesPath)) {
                propertiesPath = DEFAULT_PROPERTIES_PATH;
            }
            try {
                //1.1.2 若未传入拓展类型，则读取默认的配置文件
                Properties defaultProperties = new Properties();
                defaultProperties.load(WebCacheRefresh.class.getClassLoader().getResourceAsStream(propertiesPath));
                //1.1.3 如果当前字段未配置，则使用默认配置
                if (StringUtils.isBlank(defaultProperties.getProperty(DEFAULT_SUFF_KEY))) {
                    defaultProperties.load(ClassLoader.getSystemResourceAsStream(DEFAULT_PROPERTIES_PATH));
                }
                suffixs = defaultProperties.getProperty(DEFAULT_SUFF_KEY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //1.1 校验基础目录是否配置
        if (StringUtils.isBlank(baseDir)) {
            throw new MojoFailureException("There is no parameter that is named baseDir");
        }
        //2.扫描webapp目录下的指定类型的文件,并进行重命名
        scanFile(baseDir);
    }

    private void scanFile(String baseDir) {
        File file = new File(baseDir);
        if (file.isDirectory()) {
            File[] childNodes = file.listFiles();
            for (File childNode : childNodes) {
                if (childNode.getPath().contains("target")) {
                    continue;
                }
                if (childNode.isDirectory()) {
                    //如果是目录，则需要继续循环递归
                    scanFile(childNode.getPath());
                } else {
                    //如果不是目录，则进行判断类型
                    String fullFileName = childNode.getName();
                    int separatorIndex = childNode.getName().indexOf("-");
                    int dotIndex = childNode.getName().indexOf(".");
                    String suffix = fullFileName.substring(dotIndex);
                    String simpleFileName = childNode.getName().substring(0, dotIndex);
                    if (suffixs.contains(suffix)) {
                        //如果名字中包含-，则截取-之前的原始名称
                        String orignalName = "";
                        if (!fullFileName.contains("-")) {
                            orignalName = simpleFileName;
                        } else {
                            orignalName = simpleFileName.substring(0,separatorIndex);
                        }
                        //若当前文件在修改类型中，则对文件进行重命名
                        //生成当前日期及时间
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        String date = simpleDateFormat.format(new Date());
                        String newFileName = childNode.getParentFile().getPath() + File.separator + orignalName + "-" + date + suffix;
                        childNode.renameTo(new File(newFileName));
                    }
                }
            }
        }
    }
}
