Ember.Inflector.inflector.irregular('class', 'classes');

App = Ember.Application.create();

App.ApplicationAdapter = DS.RESTAdapter.extend({
    namespace: 'data'
});
//App.ApplicationAdapter = DS.FixtureAdapter.extend();

App.FocusView = Ember.TextField.extend({
    didInsertElement: function () {
        this.$().focus();
    }
});

Ember.Handlebars.helper('auto-focus', App.FocusView);

App.Router.map(function () {
    this.route("students", {path: "uczniowie"});
    this.route("classes", {path: "klasy"});
    this.route("classesInYear", {path: "klasy/:year"});
    this.route("courses", {path: "kursy"});
    this.route("grades", {path: "oceny"});
    this.route("competitions", {path: "konkursy"});
    this.route("teachers", {path: "nauczyciele"});
    this.route("subjects", {path: "przedmioty"});
    this.route("competition-rangs", {path: "rangi-konkursow"});
    this.route("rights", {path: "uprawnienia"});
    this.route('missing', { path: "/*path" });
});

App.Router.reopen({
    location: 'auto'
});

App.IndexRoute = Ember.Route.extend({
    redirect: function () {
        this.transitionTo('classes');
    }
});

App.MissingRoute = Em.Route.extend({
    redirect: function () {
        Em.debug('404 :: redirection to index');
        this.transitionToRoute("classes");
    }
});

App.ClassesRoute = Ember.Route.extend({
    beforeModel: function () {
        var self = this;
        return Ember.$.getJSON('/data/years').then(function (years) {
            self.transitionTo('classesInYear', years[0]);
        });
    },
    redirect: function () {
        this.refresh();
    }
});

App.ApplicationController = Ember.Controller.extend({
    selectedContentType: null,
    selectContentType: [
        {firstName: "Yehuda", id: 1},
        {firstName: "Tom", id: 2}
    ],
    test: function () {
        return this.get('selectedContentType.firstName') + " " +
            this.get('selectedDate.firstName')
    }.property('selectedContentType.firstName', 'selectedDate.firstName')
});

App.Class = DS.Model.extend({
    name: DS.attr('string'),
    yearOfCreation: DS.attr('number'),
    level: DS.attr('number')
});

App.ClassesInYearRoute = Ember.Route.extend({
    beforeModel: function () {
        var self = this;
        var classes = this.store.find('class');
        var years = Ember.$.getJSON('/data/years').then(function (years) {
            self.controllerFor('classesInYear').set('years', years);
        });
        return Ember.RSVP.all([classes, years]);
    },
    model: function (params) {
        this.controllerFor('classesInYear').set('year', parseInt(params.year));
        return this.store.filter('class', function (record) {
            return record.get('yearOfCreation') == params.year;
        });
    }
});
App.ClassesInYearController = Ember.ArrayController.extend({
    year: null,
    years: null,
    levels: [1, 2, 3],
    classes: function () {
        return this.get('model')
    }.property('year', 'model'),

    newClassName: "IA",
    newClassYearOfCreation: 2013,
    newClassLevel: 1,
    classToDelete: null,
    yearChange: function () {
        this.set('newClassYearOfCreation', this.get('year'));
        this.transitionToRoute('classesInYear', this.get('year'));
    }.observes('year'),
    actions: {
        showAddModal: function () {
            $('#add_window').modal('show');
        },
        add: function () {
            this.store.createRecord('class', {
                name: this.get('newClassName'),
                yearOfCreation: this.get('newClassYearOfCreation'),
                level: this.get('newClassLevel')
            }).save();
        },
        cancelDelete: function () {
            $('#delete_window').modal('hide');
        },
        'delete': function () {
            var myClass =  this.get('classToDelete');
            myClass.destroyRecord();
        }
    }
});

App.ClassController = Ember.ObjectController.extend({
    needs: ['classesInYear'],
    isEditing: false,
    actions: {
        'delete': function () {
            var myClass = this.get('model');
            this.get('controllers.classesInYear').set('classToDelete', myClass);
            var name = myClass.get('name') + " (" + myClass.get('yearOfCreation') + ") ";
            $('#delete_window_class').text(name);
            $('#delete_window').modal('show');
        },
        edit: function () {
            this.set('isEditing', true)
        },
        accept: function () {
            this.set('isEditing', false);
            this.get('model').save();
        },
        toggle: function () {
            if (this.get('isEditing')) {
                this.set('isEditing', false);
                this.get('model').save();
            } else {
                this.set('isEditing', true)
            }
        }
    }
});