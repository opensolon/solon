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

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.afterturn.easypoi.util.WebFilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.noear.solon.annotation.Component;

import cn.afterturn.easypoi.entity.vo.PDFTemplateConstants;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.pdf.PdfExportUtil;
import cn.afterturn.easypoi.pdf.entity.PdfExportParams;
import org.noear.solon.core.handle.Context;

/**
 * PDF 导出 View
 * @author JueYue
 *  2016年1月19日 上午10:43:02
 */
@Component(PDFTemplateConstants.EASYPOI_PDF_TEMPLATE_VIEW)
public class EasypoiPDFTemplateView extends PoiBaseView {

    public EasypoiPDFTemplateView() {
        setContentType("application/pdf");
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected final void renderOutputModel(Map<String, Object> model, Context ctx) throws Exception {
        String fileName = "临时文件";
        ByteArrayOutputStream baos = createTemporaryOutputStream();
        PdfExportParams entity = (PdfExportParams) model.get(PDFTemplateConstants.PARAMS);
        Class<?> pojoClass = (Class<?>) model.get(PDFTemplateConstants.CLASS);
        Collection<?> dataSet = (Collection<?>) model.get(PDFTemplateConstants.DATA_LIST);
        List<ExcelExportEntity> entityList = (List<ExcelExportEntity>) model
            .get(PDFTemplateConstants.ENTITY_LIST);
        if (entityList == null) {
            PdfExportUtil.exportPdf(entity, pojoClass, dataSet, baos);
        } else {
            PdfExportUtil.exportPdf(entity, entityList, (Collection<? extends Map<?, ?>>) dataSet,
                baos);
        }
        String userFileName = (String) model.get(PDFTemplateConstants.FILE_NAME);
        if (StringUtils.isNoneBlank(userFileName)) {
            fileName = userFileName;
        }
        // 用工具类生成符合RFC 5987标准的文件名header, 去掉UA判断
        ctx.header("content-disposition", WebFilenameUtils.disposition(fileName + ".pdf"));
        writeToResponse(ctx, baos);
    }

}
