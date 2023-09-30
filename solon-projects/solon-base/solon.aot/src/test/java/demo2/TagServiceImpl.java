package demo2;

import org.noear.solon.annotation.Component;

/**
 * @author noear 2022/9/30 created
 */
@Component
public class TagServiceImpl extends CrudServiceImpl<TagDao, Tag, TagDTO, TagConvert> implements TagService {

    @Override
    public TagDTO update(Tag tag) {
        //return super.update(tag);
        System.out.println("hello");
        return null;
    }

    @Override
    public TagDTO update(TagDTO tagDTO) {
        //return super.update(tag);
        System.out.println("hello1");
        return null;
    }
}