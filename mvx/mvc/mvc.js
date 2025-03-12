/*
MVC 模式
- Model：负责业务逻辑和数据处理，直接与数据库或数据源交互。
- View：负责渲染用户界面，显示数据给用户。View可与Model绑定。
- Controller：作为用户输入的中介，接收用户的输入并更新 Model 或 View。
*/

// Model (数据模型)
class Model {
    constructor() {
        this.name = "MVC Basic";
        this.num = 1;
    }

    getName() {
        return this.name;
    }

    setNum(num) {
        this.num = num;
    }

    getNum() {
        return this.num;
    }

    incrementNum(value) {
        this.num += value;
    }

    setName(name) {
        this.name = name;
    }
}

// View (视图层)
class View {
    // MVC模式下 View 可关联 Model
    constructor(model) {
        this.$el = {};
        this.tpl = '<div><b>${ name }</b><em>${ num }</em></div>';
        this.model = model || {};
    }

    compile(data) {
        return this.tpl.replace(/\$\{([\s\S]+?)\}/g, (_, key) => {
            const value = data[key.trim()];
            return value !== undefined && value !== null ? value : '';
        });
    }

    // 渲染数据
    render(data) {
        this.$el.innerHTML = this.compile(data);
    }

    // 设置按钮点击事件监听
    setClickListener(callback) {
        this.$el.addEventListener('click', callback);
    }

    // 设置鼠标移出事件监听
    setMouseOutListener(callback) {
        this.$el.addEventListener('mouseout', (event) => {
            if (!this.$el.contains(event.relatedTarget)) {
                event.stopPropagation(); // 阻止冒泡
                callback(event);
            }
        });
    }

    // 更新视图显示，直接从 Model 获取数据
    updateView() {
        this.render(this.model);
    }
}

// Controller (控制器)
class Controller {
    constructor(model, view) {
        this.model = model;
        this.view = view;
    }

    init() {
        // 给view设置事件回调
        this.view.setClickListener(this.onButtonClick.bind(this));
        this.view.setMouseOutListener(this.onMouseOut.bind(this));
        this.updateView();
    }

    onButtonClick(evt) {
        // console.log(evt, this)
        this.model.setName("onButtonClick");
        this.model.incrementNum(1);
        this.updateView();
    }

    onMouseOut(evt) {
        // console.log(evt, this)
        this.model.setName("onMouseOut");
        this.model.setNum(0);
        this.updateView();
    }

    // 由 Controller 调用更新 View
    updateView() {
        console.log(`<div><b>${this.model.getName()}</b><em>${this.model.getNum()}</em></div>`);
        this.view.updateView();
    }
}

// Test
(function test() {
    const model = new Model();
    // View 可以持有 Model，或在更新视图时传递Model
    const view = new View(model);
    const controller = new Controller(model, view);
    // controller.init();

    console.log("Test 1: Button Click");
    controller.onButtonClick();

    console.log("Test 2: Mouse Out");
    controller.onMouseOut();
})();

/*
jarry@MacBook-Pro mvc % node mvc.js
Test 1: Button Click
<div><b>onButtonClick</b><em>2</em></div>
Test 2: Mouse Out
<div><b>onMouseOut</b><em>0</em></div>
*/