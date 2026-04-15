# design-patterns 概述

**欢迎来到设计模式与编程范式站点，这里专门探索基于各种语言的设计模式与编程范式，力求打造最佳编程实践。**

# 目录结构
## 23种经典设计模式源码实例
[https://github.com/microwind/design-patterns](https://github.com/microwind/design-patterns)

## 设计模式7大原则源码实例
[https://github.com/microwind/design-patterns/tree/main/design-principles](https://github.com/microwind/design-patterns/tree/main/design-principles)

## 几种编程范式实例详解
[https://github.com/microwind/design-patterns/tree/main/programming-paradigm](https://github.com/microwind/design-patterns/tree/main/programming-paradigm)

## 分层架构设计模式实例详解
[https://github.com/microwind/design-patterns/tree/main/mvx](https://github.com/microwind/design-patterns/tree/main/mvx)

---

# MVC例子
 [mvc+mvp+mvvp DEMO例子](./mvc_mvp_mvvm.html)

## Java 版本
###  Java servlet MVC 版本，这里采用最基本的原理来测试，Spring或Struts道理类似
```java
  // Model即业务实体对象，可以增添少许方法来处理数据
  // src/mvc/Model.java
  public class Model {
      private String name;
      private Integer num;
      public Model() {
      }
      public Model(String name, Integer num) {
          this.name = name;
          this.num = num;
      }
      public String getName() {
          return this.name;
      }
      public Integer getNum() {
          return this.num;
      }
  }

  // Processing bussiness logic，业务逻辑处理，组织与处理Model，此类可选
  // src/mvc/ModelService.java
  import java.util.Optional;
  public class ModelService {
      Model model;
      public Optional<Model> getModel() {
          if (this.model == null) {
              this.model = new Model("Thomas", 10);
          }
          // do something
          return Optional.of(this.model);
      }
  }

  // Controller，负责控制转发和模板映射。采用WebServlet注解，也可通过web.xml配置
  // src/mvc/Controller.java
  import javax.servlet.RequestDispatcher;
  import javax.servlet.ServletException;
  import javax.servlet.annotation.WebServlet;
  import javax.servlet.http.HttpServlet;
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;
  import java.io.IOException;

  @WebServlet(
          name = "ModelServlet",
          urlPatterns = "/hello-model")
  public class Controller extends HttpServlet {
      private ModelService modelService = new ModelService();
      private void processRequest(
          HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
          modelService.getModel().ifPresent(s -> {
              request.setAttribute("helloModel", s);
          });
          RequestDispatcher dispatcher = request.getRequestDispatcher(
                  "/WEB-INF/template/model.jsp");
          dispatcher.forward(request, response);
      }

      @Override
      protected void doGet(
              HttpServletRequest request, HttpServletResponse response)
              throws ServletException, IOException {
          processRequest(request, response);
      }

      @Override
      protected void doPost(
              HttpServletRequest request, HttpServletResponse response)
              throws ServletException, IOException {
          processRequest(request, response);
      }
  }

  // /WEB-INF/template/model.jsp
  // View，使用JSP
  <%@ page import="mvc.Model" %>
  <html>
  <head>
    <title>Hello Model</title>
  </head>
  <body>
  <h1>Hello Model</h1>
  <%
    if (request.getAttribute("helloModel") != null) {
        System.out.print(new Model());
        Model model = (Model) request.getAttribute("helloModel");
  %>
  <div>Name: <%= model.getName()%></div>
  <div>Num: <%= model.getNum()%></div>
  <% } %>
  </body>
  </html>
  // Java MVC版结束
```

## JS 版本
###  1. MVC Basic JS版: 最基本版MVC，与后端MVC类似, M与C分离。事件绑定和数据更新均通过Controller来设置和转发。

```js
  !(function(win, doc) {
    // View
    class View {
      constructor($el, tpl) {
        this.$el = $el
        this.tpl = tpl
      }
      render(data) {
        this.$el.innerHTML = this.compile(data)
      }
      compile(data) {
        return this.tpl || `<div><b>${data.name}</b>
         <em>${data.num}</em></div>`
      }
    }
    // Model
    class Model {
      constructor() {
        this.data = { name: 'MVC Basic', num: 1 }
      }
      update(value, cb) {
        const fn = (res) => {
          this.data.num += value
          cb(this.data)
        }
        fetch('./').then(fn).catch(fn)
      }
      change(name, cb) {
        this.data.name = name
        cb(this.data)
      }
    }
    // Controller
    class Controller {
      constructor(view, model) {
        this.view = view
        this.model = model
      }
      init() {
        this.view.$el.addEventListener('click', evt => {
          this.model.data.name = 'Click to add:Event dispatch.'
          this.model.update(1, data => this.view.render(data))
        })
        this.view.$el.addEventListener('mouseout', evt => {
          this.model.change('Click to add', data => this.view.render(data))
        })
        this.view.render(this.model.data)
      }
    }
    const model = new Model()
    const view = new View(doc.querySelector('#mvc-basic-view'))
    const controller = new Controller(view, model)
    controller.init()
  })(window, document)
```

### 2. MVC:前端MVC，与后端MVC并不太一样，View与Model是绑定的。在View中监测Model的行为通知来更新视图，Model更新则通过Controller注册转发
```js
  !(function(win, doc) {
    class Observer {
      constructor() {
        this.observers = []
      }
      register(callback) {
        this.observers.push(callback)
      }
      notify(args) {
        for (let i = 0, l = this.observers.length; i < l; i++) {
          this.observers[i](args)
        }
      }
    }
    // View
    class View {
      constructor($el, model, tpl) {
        this.$el = $el
        this.model = model
        this.tpl = tpl
        this.onAdd = new Observer(this)
        this.onChange = new Observer(this)
        this.model.onUpdate.register(value => this.render())
        this.model.onChange.register(name => this.render())
        this.$el.addEventListener('click', evt => this.onAdd.notify(1))
        this.$el.addEventListener('mouseout', evt => this.onChange.notify('Click to add'))
      }
      render() {
        this.$el.innerHTML = this.compile(this.model.data)
      }
      compile(data) {
        return this.tpl || `<div><b>${data.name}</b>
         <em>${data.num}</em></div>`
      }
    }
    // Model
    class Model {
      constructor() {
        this.data = { name: 'MVC', num: 1 }
        this.onUpdate = new Observer(this)
        this.onChange = new Observer(this)
      }
      update(value) {
        const fn = (res) => {
          this.data.num += value
          this.onUpdate.notify(value)
        }
        fetch('./').then(fn).catch(fn)
      }
      change(name) {
        this.data.name = name
        this.onChange.notify(name)
      }
    }
    // Controller
    class Controller {
      constructor(view, model) {
        this.view = view
        this.model = model
      }
      init() {
        this.view.onAdd.register(value => {
          this.model.data.name = 'Click to add:Event dispatch.'
          this.model.update(value)
        })
        this.view.onChange.register(name => this.model.change(name))
        this.view.render()
      }
    }
    const model = new Model()
    const view = new View(doc.querySelector('#mvc-view'), model)
    const controller = new Controller(view, model)
    controller.init()
  })(window, document)
```

### 3. MVP:Contorller升级为Presenter，将View与Model分离。通过Presenter来绑定View事件和代理双方行为
```js
  !(function(win, doc) {
    // View
    class View {
      constructor($el, tpl) {
        this.$el = $el
        this.tpl = tpl
      }
      render(data) {
        this.$el.innerHTML = this.compile(data)
      }
      compile(data) {
        return this.tpl || `<div><b>${data.name}</b>
         <em>${data.num}</em></div>`
      }
    }
    // Model
    class Model {
      constructor() {
        this.data = { name: 'MVP', num: 1 }
      }
      update(value) {
        const fn = (res) => {
          this.data.num += value
          return res
        }
        return fetch('./').then(fn).catch(fn)
      }
      change(name) {
        this.data.name = name
      }
    }
    // Presenter
    class Presenter {
      constructor(view, model) {
        this.view = view
        this.model = model
        view.$el.addEventListener('click', evt => this.add(1))
        view.$el.addEventListener('mouseout', evt => this.change('Click to add'))
      }
      init() {
        this.render(this.model.data)
      }
      render(data) {
        this.view.render(data)
      }
      add(value) {
        this.model.data.name = 'Click to add:Event proxy.'
        this.model.update(value).then(res => {
          this.render(this.model.data)
        })
      }
      change(name) {
        this.model.change(name)
        this.render(this.model.data)
      }
    }
    const view = new View(doc.querySelector('#mvp-view'))
    const presenter = new Presenter(view, new Model())
    presenter.init()
  })(window, document)
```

### 4. MVP+Observer: 增加Observer，View与Model分离。事件由View绑定，Presenter增加对应函数，代理View与Model全部行为
```js
  !(function(win, doc) {
    class Observer {
      constructor() {
        this.observers = []
      }
      register(callback) {
        this.observers.push(callback)
      }
      notify(args) {
        for (let i = 0, l = this.observers.length; i < l; i++) {
          this.observers[i](args)
        }
      }
    }
    // View
    class View {
      constructor($el, tpl) {
        this.$el = $el
        this.tpl = tpl
        this.onAdd = new Observer(this)
        this.onChange = new Observer(this)
        this.$el.addEventListener('click', evt => this.onAdd.notify(1) )
        this.$el.addEventListener('mouseout', evt => this.onChange.notify('Click to add'))
      }
      render(data) {
        this.$el.innerHTML = this.compile(data)
      }
      compile(data) {
        return this.tpl || `<div><b>${data.name}</b>
         <em>${data.num}</em></div>`
      }
    }
    // Model
    class Model {
      constructor() {
        this.data = { name: 'MVP+Observer', num: 1 }
        this.onUpdate = new Observer(this)
        this.onChange = new Observer(this)
      }
      update(value) {
        const fn = (res) => {
          this.data.num += value
          this.onUpdate.notify(value)
          return res
        }
        return fetch('./').then(fn).catch(fn)
      }
      change(name) {
        this.data.name = name
        this.onChange.notify(name)
      }
    }
    // Presenter
    class Presenter {
      constructor(view, model) {
        this.view = view
        this.model = model
        this.view.onAdd.register(value => this.add(value))
        this.view.onChange.register(name => this.change(name))
        this.model.onUpdate.register(res => this.render())
        this.model.onChange.register(res => this.render())
      }
      init() {
        this.render()
      }
      render() {
        this.view.render(this.model.data)
      }
      add(value) {
        this.model.data.name = 'Click to add:Event proxy.'
        this.model.update(value)
      }
      change(name) {
        this.model.change(name)
      }
    }
    const $el = doc.querySelector('#mvp-observer-view')
    const presenter = new Presenter(new View($el), new Model())
    presenter.init()
  })(window, document)
```

### 5. MVP+Dispatch: 轻量级的MVP，与最基本MVC也类似，将View与Model分离，通过Presenter注册Model行为，但不增加代理函数，直接将View与Model绑定
```js
  !(function(win, doc) {
    class Observer {
      constructor() {
        this.observers = []
      }
      register(callback) {
        this.observers.push(callback)
      }
      notify(args) {
        for (let i = 0, l = this.observers.length; i < l; i++) {
          this.observers[i](args)
        }
      }
    }
    // View
    class View {
      constructor($el, tpl) {
        this.$el = $el
        this.tpl = tpl
        this.onAdd = new Observer(this)
        this.onChange = new Observer(this)
      }
      render(data) {
        this.$el.innerHTML = this.compile(data)
      }
      compile(data) {
        return this.tpl || `<div><b>${data.name}</b>
         <em>${data.num}</em></div>`
      }
    }
    // Model
    class Model {
      constructor() {
        this.data = { name: 'MVP+Dispatch', num: 1 }
        this.onUpdate = new Observer(this)
        this.onChange = new Observer(this)
      }
      update(value) {
        const fn = (res) => {
          this.data.num += value
          this.onUpdate.notify(value)
        }
        fetch('./').then(fn).catch(fn)
      }
      change(name) {
        this.data.name = name
        this.onChange.notify(name)
      }
    }
    // Controller
    class Controller {
      constructor(view, model) {
        this.view = view
        this.model = model
      }
      init() {
        this.view.$el.addEventListener('click', evt => this.view.onAdd.notify(1))
        this.view.$el.addEventListener('mouseout', evt => this.view.onChange.notify('Click to add'))
        this.view.onAdd.register(value => {
          this.model.data.name = 'Click to add:Event dispatch.'
          this.model.update(value)
        })
        this.view.onChange.register(name => this.model.change(name))
        this.model.onUpdate.register(value => this.view.render(this.model.data))
        this.model.onChange.register(name => this.view.render(this.model.data))
        this.view.render(this.model.data)
      }
    }
    const model = new Model()
    const view = new View(doc.querySelector('#mvp-dispatch-view'))
    const controller = new Controller(view, model)
    controller.init()
  })
```

### 6. 前端MVVM模式，通过观察者模式将数据与视图进行双向绑定，然后由数据驱动更改
```js
  !(function(win, doc) {
    class Agent {
      constructor(target, watch) {
        const handle = {
          set(obj, prop, value) {
           if (obj[prop] !== value && obj.hasOwnProperty(prop)) {
             obj[prop] = value
             watch && watch(obj, prop, value)
           }
           return Reflect.set(obj, prop, value)
          },
          get(obj, prop) {
            if (typeof obj[prop] === 'object' && obj[prop] !== null) {
              return new Proxy(obj[prop], handle)
            } else {
              return Reflect.get(obj, prop)
            }
          }
        }
        return new Proxy(target, handle)
      }
    }
    // View
    class View {
      constructor($el, tpl) {
        this.$el = $el
      }
      compile(data) {
        return this.tpl || `
         <div><b>${data.name}</b>
         <em>${data.num}</em></div>`
      }
      render(data) {
        this.$el.innerHTML = this.compile(data)
      }
    }
    // Model
    class Model {
      constructor() {
        this.data = { name: 'MVVM', num: 1 }
      }
      update(data) {
        const fn = (res) => {
          data.num += 1
          return data
        }
        return fetch('./').then(fn).catch(fn)
      }
    }
    // Controller
    class ViewModel {
      constructor({ $el, view, model }) {
        this.view = view
        this.data = new Agent(model.data, (obj, prop, value) => {
          this.view.render(this.data)
        })
        this.model = model
        this.init()
      }
      init() {
        console.log('render:', this.data)
        this.view.$el.addEventListener('click', (evt) => this.add(evt))
        this.view.$el.addEventListener('mouseout', (evt) => this.change(evt))
        this.view.render(this.data)
      }
      change(evt) {
        this.data.name = 'Click to add'
      }
      add(evt) {
        this.data.name = 'Click to add:Watch data.'
        this.model.update(this.data)
      }
    }
    const viewModel = new ViewModel({
      view: new View(doc.querySelector('#mvvm-view')), 
      model: new Model()
    })
  })(window, document)
```

### 7. 前端MVVM最简版框架模式，增加事件标记提取绑定和Watch行为变化，通过观察者模式将数据与视图进行双向绑定，然后由数据驱动更改
```js
  !(function(win, doc) {
    class Observer {
      constructor(vm) {
        this.vm = vm
        this.observers = []
      }
      register(callback) {
        this.observers.push(callback)
      }
      notify(args) {
        for (let i = 0, l = this.observers.length; i < l; i++) {
          this.observers[i](args)
        }
      }
    }
    class Watcher {
      constructor(vm, cb) {
        this.vm = vm
        this.cb = cb
        return this.run.bind(this)
      }
      run(obj, prop, value) {
        console.log('watch:', arguments)
        if (this.vm.watch[prop]) {
          (this.vm.watch[prop]).call(this.vm.data, value, this.vm)
        }
        this.cb.call(this.vm, obj, prop, value, this)
      }
    }
    class Agent {
      constructor(target, observer) {
        const handle = {
          set(obj, prop, value) {
           if (obj[prop] !== value && obj.hasOwnProperty(prop)) {
             obj[prop] = value
             observer && observer.notify(obj, prop, value)
           }
           return Reflect.set(obj, prop, value)
          },
          get(obj, prop) {
            if (typeof obj[prop] === 'object' && obj[prop] !== null) {
              return new Proxy(obj[prop], handle)
            } else {
              return Reflect.get(obj, prop)
            }
          }
        }
        return new Proxy(target, handle)
      }
    }
    function template(strings, ...keys) {
      return function(...values) {
        const dict = values[values.length - 1] || {}
        const result = [strings[0]]
        keys.forEach((key, i) => {
          let value = dict[key]
          result.push(value, strings[i + 1])
        })
        return result.join('')
      }
    }

    // View
    class View {
      constructor($el, tpl) {
        this.$el = $el
        this.tpl = tpl
        this.events = []
      }
      compile(data) {
        const content = this.tpl(data)
        let reg = new RegExp('e-([a-z]+)\=\"([a-z0-9]+)' + '\"', 'igm')
        const result = content.match(reg) || []
        result.forEach((item) => {
          const [evt, fn] = item.split('=')
          this.events.push([evt.substr(2), fn.substr(0, fn.length - 1).substr(1)])
        })
        return content
      }
      render(data) {
        this.$el.innerHTML = this.compile(data)
      }
    }
    const Model = { name: 'Tiny MVVM Framework', num: 1 }
    // ViewModel
    class TinyMVVM {
      constructor({ $el, tpl, data, methods = {}, watch = {}, computed = {} }) {
        this.view = new View($el, tpl)
        this.observer = new Observer(this)
        this.watcher = new Watcher(this, (obj, prop, value) => {
          this.render()
        })
        this.observer.register(this.watcher)
        this.data = new Agent(data(), this.observer)
        this.methods = methods
        this.watch = watch
        this.computed = computed
        this.init()
      }
      render() {
        const data = Object.assign({}, this.data)
        for (let attr in this.computed) {
          data[attr] = (this.computed[attr]).call(this.data, this)
        }
        this.view.render(data)
      }
      init() {
        this.render()
        this.mount()
      }
      mount() {
        this.view.events.forEach((item) => {
          this.view.$el.addEventListener(item[0], (evt) => (this.methods[item[1]].bind(this))(evt))
        })
      }
    }
    new TinyMVVM({
      $el: doc.querySelector('#mvvm-watch-view'),
      tpl: template`
         <div e-click="add" e-mouseout="change"><b>${'name'}</b>
         <em>${'num'}</em> | ${'computedProp'}</div>`,
      data() {
        return Model
      },
      methods: {
        fetch(value) {
          const fn = (res) => {
            this.data.num += value
            return this.data
          }
          return fetch('./').then(fn).catch(fn)
        },
        change(evt) {
          this.data.name = 'Click to add'
        },
        add(evt) {
          this.data.name = 'Click to add:Watch data.'
          this.methods.fetch.bind(this)(1)
        }
      },
      computed: {
        computedProp: function(vm) {
          return this.name + ' computed.'
        }
      },
      watch: {
        num(value, vm) {
          this.name = 'Click to add:Watch data.'
        }
      }
    })
  })(window, document)
```
