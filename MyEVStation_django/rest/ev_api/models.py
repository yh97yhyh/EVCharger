# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey and OneToOneField has `on_delete` set to the desired behavior
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models


class Stations(models.Model):
    id = models.CharField(db_column='id', max_length=8, blank=True, primary_key=True, null=False)  # Field name made lowercase.
    statNm = models.CharField(db_column='statNm', max_length=100, blank=True, null=True)  # Field name made lowercase.
    chgerId = models.CharField(db_column='chgerId', max_length=2, blank=True, null=True)  # Field name made lowercase.
    chgerType = models.CharField(db_column='chgerType', max_length=2, blank=True, null=True)  # Field name made lowercase.
    addr = models.CharField(max_length=150, blank=True, null=True)
    lat = models.CharField(max_length=20, blank=True, null=True)
    lng = models.CharField(max_length=20, blank=True, null=True)
    useTime = models.CharField(db_column='useTime', max_length=50, blank=True, null=True)  # Field name made lowercase.
    busiId = models.CharField(db_column='busiId', max_length=2, blank=True, null=True)  # Field name made lowercase.
    busiNm = models.CharField(db_column='busiNm', max_length=50, blank=True, null=True)  # Field name made lowercase.
    busicall = models.CharField(db_column='busiCall', max_length=20, blank=True, null=True)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'stations'
