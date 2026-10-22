package learn.springboot.common;

import learn.springboot.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;   // 当前页数据
    private long total;      // 总条数（算总页数用）
    private int  page;       // 当前页
    private int  size;       // 每页条数
    public PageResult(List<T> list, long total, int page, int size) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.size = size;
    }
}
