/**
 * Copyright 2013-2015 JueYue (qrb.jueyue@gmail.com)
 *   
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.afterturn.easypoi.view;

import java.io.OutputStream;
import java.util.Map;

import cn.afterturn.easypoi.util.WebFilenameUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.noear.solon.annotation.Component;

import cn.afterturn.easypoi.entity.vo.TemplateWordConstants;
import cn.afterturn.easypoi.word.WordExportUtil;
import org.noear.solon.core.handle.Context;

/**
 * Word模板视图
 * 
 * @author JueYue
 *  2014年6月30日 下午9:15:49
 */
@SuppressWarnings("unchecked")
@Component(TemplateWordConstants.EASYPOI_TEMPLATE_WORD_VIEW)
public class EasypoiTemplateWordView extends PoiBaseView {

    private static final String CONTENT_TYPE = "application/msword";

    public EasypoiTemplateWordView() {
        setContentType(CONTENT_TYPE);
    }

    @Override
    protected void renderOutputModel(Map<String, Object> model, Context ctx) throws Exception {
        String codedFileName = "临时文件.docx";
        if (model.containsKey(TemplateWordConstants.FILE_NAME)) {
            codedFileName = (String) model.get(TemplateWordConstants.FILE_NAME) + ".docx";
        }
        // 用工具类生成符合RFC 5987标准的文件名header, 去掉UA判断
        ctx.headerSet("content-disposition", WebFilenameUtils.disposition(codedFileName));
        XWPFDocument document = WordExportUtil.exportWord07(
            (String) model.get(TemplateWordConstants.URL),
            (Map<String, Object>) model.get(TemplateWordConstants.MAP_DATA));
        OutputStream out = ctx.outputStream();
        document.write(out);
        out.flush();
    }
}
