(function($CQ, _, Backbone, SCF) {"use strict";
	var TaskBoxView = SCF.View.extend({
		viewName : "TaskBox",
		init : function() {
			this.listenTo(this.model, "change", this.render);
			this.listenTo(this.model, "model:loaded", this.render);
			this.listenTo(this.model.get("items"), "remove", function() {
				var size = this.model.get("totalSize");
				this.model.set({
					"totalSize" : size - 1
				}, {
					silent : true
				});
				this.render();
			});
		},
		requiresSession : true,
		toggleComposer : function() {
			this.$el.find(".scf-js-composer-block:first").toggle();
			this.focus("title");
		},
		addProject : function(e) {
			//TODO: add validation here
			var desc = this.getField("description");
			var title = this.getField("title");
			var data = {
				"title" : title,
				"description" : desc
			};
			this.model.addProject(data);
			e.preventDefault();
			return false;
		},
		showMyProjects : function(e) {
			e.preventDefault();
			this.model.filterByUser();
		},
		showAllProjects : function(e) {
			e.preventDefault();
			this.model.fetch();
			this.model.set({
				"filtered" : false
			}, {
				silent : true
			});
		}
	});

	var TaskBox = SCF.Model.extend({
		modelName : "TaskBoxModel",
		relationships : {
			"items" : {
				collection : "ProjectList",
				model : "ProjectModel"
			}
		},
		addProject : function(data) {
			var that = this;
			var success = function(response) {
				that.log.debug("added project!");
				var ProjectKlass = SCF.Models[that.constructor.prototype.relationships.items.model];
				var newProject = ProjectKlass.createLocally(response.response);
				var projects = that.get("items");
				projects.push(newProject);
				var size = that.get("totalSize");
				that.set({
					"items" : projects,
					totalSize : size + 1
				});
			};
			var error = function(e) {
				that.log.error("Unable to add project");
			};
			var postData = data;
			postData[":operation"] = "social:samples:createProject";
			$CQ.ajax(SCF.config.urlRoot + this.get("id") + SCF.constants.URL_EXT, {
				dataType : "json",
				type : "POST",
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				xhrFields : {
					withCredentials : true
				},
				data : this.addEncoding(postData),
				"success" : success,
				"error" : error
			});
		},
		filterByUser : function() {
			this.fetch({
				data : $.param({
					filter : "onlyMy"
				})
			});
			this.set("filtered", true);
		}
	});

	var ProjectList = Backbone.Collection.extend({
		collectionName : "ProjectList"
	});

	SCF.registerComponent('social/samples/components/tasks/taskbox', TaskBox, TaskBoxView);

})($CQ, _, Backbone, SCF);
