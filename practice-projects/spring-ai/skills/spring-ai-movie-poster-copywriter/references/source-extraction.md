# Source Extraction Rules

## Target Sources

- Douban movie detail page (primary)
- Chinese encyclopedia page (Baidu Baike or equivalent, secondary)

## Keep Fields

- Basic identity: title aliases, year, region, genre
- Plot hooks: conflict, objective, stakes (no ending)
- Creative signals: director/cast keywords, visual style tags
- Public recognition: awards/shortlists (if reliable)

## Drop Fields

- Long user comments
- Full plot summaries with ending details
- Marketing fluff with no factual signal

## Recommended Normalized Context Format

```text
片名：...
类型与背景：...
核心冲突：...
气质关键词：...
演员/主创关键词：...
荣誉信息：...
```

Keep normalized context under 1200 Chinese characters.
