/*
Copyright (c) 2003-2011, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

CKEDITOR.editorConfig = function( config )
{
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
config.language='zh-cn';//中文
config.toolbarStartupExpanded =false;
config.enterMode = CKEDITOR.ENTER_BR;   
config.shiftEnterMode = CKEDITOR.ENTER_P; 
config.skin = 'office2003'; 
};
