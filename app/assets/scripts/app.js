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

App.RestElementController = Ember.ObjectController.extend({
    init: function () {
        this.set('needs', [this.get('parent')])
    },
    isEditing: false,
    actions: {
        'delete': function () {
            var element = this.get('model');
            this.get('controllers.' + this.get('parent')).set('elementToDelete', element);
            var name = element.get('name') + " (" + element.get('yearOfCreation') + ") ";
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

App.RestController = Ember.ArrayController.extend({
    init: function () {
        this.set('elementToCreate', [this.get('defaultElementToCreate')])
    },
    restoreElementToCreate: function () {
        var defaultElement = this.get('defaultElementToCreate');
        var newElement = this.get('elementToCreate');
        console.log("ej", defaultElement);
        for (var key in defaultElement) {
            if (defaultElement[key] !== null) {
                console.log(newElement[key]);
                console.log(defaultElement[key]);
                this.set('elementToCreate.' + key, defaultElement[key]);
            }
        }
    },
    elementToCreate: null,
    elementToDelete: null,
    actions: {
        showAddModal: function () {
            $('#add_window').modal('show');
        },
        add: function () {
            this.store.createRecord(this.get('className'), this.get('elementToCreate')).save();
            this.restoreElementToCreate();
        },
        cancelAdd: function () {
            this.restoreElementToCreate();
        },
        cancelDelete: function () {
            $('#delete_window').modal('hide');
        },
        'delete': function () {
            var myClass = this.get('elementToDelete');
            myClass.destroyRecord();
        }
    }
});

App.Class = DS.Model.extend({
    name: DS.attr('string'),
    yearOfCreation: DS.attr('number'),
    level: DS.attr('number')
});
App.ClassController = App.RestElementController.extend({
    parent: 'classesInYear'
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
App.ClassesInYearController = App.RestController.extend({
    className: 'class',
    defaultElementToCreate: {
        name: "",
        yearOfCreation: null,
        level: null
    },
    year: null,
    years: null,
    levels: [1, 2, 3],
    yearChange: function () {
        this.set('elementToCreate.yearOfCreation', this.get('year'));
        this.transitionToRoute('classesInYear', this.get('year'));
    }.observes('year')
});

App.Subject = DS.Model.extend({
    name: DS.attr('string')
});
App.SubjectController = App.RestElementController.extend({
    parent: 'subjects'
});

App.SubjectsRoute = Ember.Route.extend({
    model: function () {
        return this.store.find('subject');
    }
});
App.SubjectsController = App.RestController.extend({
    className: 'subject',
    defaultElementToCreate: {
        name: ""
    }
});